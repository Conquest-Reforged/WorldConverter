package me.dags.converter.converter.world;

import me.dags.converter.converter.config.CustomData;
import me.dags.converter.data.GameData;
import me.dags.converter.data.Mappings;
import me.dags.converter.converter.Converter;
import me.dags.converter.converter.world.level.Level;
import me.dags.converter.converter.world.level.LevelTask;
import me.dags.converter.converter.world.region.RegionTask;
import me.dags.converter.util.CopyTask;
import me.dags.converter.data.GameDataUtil;
import me.dags.converter.util.IO;
import me.dags.converter.util.Threading;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.MinecraftVersion;
import me.dags.converter.version.Version;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

public class WorldConverter {

    private final File root;
    private final Level level;
    private final CustomData customData;

    public WorldConverter(File root, CustomData customData) {
        this.root = root;
        this.level = new Level(new File(root, "level.dat"));
        this.customData = customData;
    }

    private Version getWorldVersion(Version version) throws IOException {
        if (version == MinecraftVersion.DETECT) {
            return level.getVersion();
        }
        return version;
    }

    private GameData getSourceGameData(Version version) throws Exception {
        File data = new File(root, "game_data.json");
        if (customData.dataIn.get().exists()) {
            data = customData.dataIn.get();
        }
        return GameDataUtil.loadGameData(data, version);
    }

    private GameData getDestGameData(Version from, Version to, GameData source) throws Exception {
        if (to == from) {
            return source;
        }
        return GameDataUtil.loadGameData(customData.dataOut.get(), to);
    }

    public List<Future<Void>> convert(Version from, Version to, File dest) throws Exception {
        Version sourceVersion = getWorldVersion(from);
        GameData sourceData = getSourceGameData(sourceVersion);
        GameData destData = getDestGameData(sourceVersion, to, sourceData);
        Mappings mappings = Mappings.build(sourceData, destData).builtIn();
        GameData gameData = GameDataUtil.applyMappings(customData, mappings);
        Converter converter = new ChunkConverter(from, to, gameData);
        CollectorContext context = new CollectorContext(to, destData, converter);
        collect(root, dest, context, 0);
        return context.tasks;
    }

    private void collect(File source, File dest, CollectorContext context, int depth) {
        if (depth++ > 5) {
            return;
        }

        if (source.getName().startsWith(".") || source.getName().endsWith("_old")) {
            return;
        }

        dest = new File(dest, source.getName());

        if (source.isDirectory()) {
            for (File file : IO.list(source)) {
                collect(file, dest, context, depth);
            }
        } else if (source.getName().endsWith(".mca")) {
            Logger.log("Queuing region task:", source.getName());
            RegionTask task = new RegionTask(source, dest, context.converter);
            context.tasks.add(Threading.submit(task));
        } else if (source.getName().equals("level.dat")) {
            Logger.log("Queuing level.dat task:", source.getName());
            LevelTask task = new LevelTask(source, dest, context.version, context.gameData);
            context.tasks.add(Threading.submit(task));
        } else {
            Logger.log("Queuing copy task:", source.getName());
            CopyTask task = new CopyTask(source, dest);
            context.tasks.add(Threading.submit(task));
        }
    }

    private static class CollectorContext {

        private final List<Future<Void>> tasks = new LinkedList<>();
        private final Version version;
        private final GameData gameData;
        private final Converter converter;

        private CollectorContext(Version version, GameData gameData, Converter converter) {
            this.version = version;
            this.gameData = gameData;
            this.converter = converter;
        }
    }
}
