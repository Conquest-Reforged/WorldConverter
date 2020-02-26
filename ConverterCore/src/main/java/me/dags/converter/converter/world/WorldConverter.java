package me.dags.converter.converter.world;

import me.dags.converter.converter.Converter;
import me.dags.converter.converter.ConverterData;
import me.dags.converter.converter.config.CustomData;
import me.dags.converter.converter.world.level.Level;
import me.dags.converter.converter.world.level.LevelTask;
import me.dags.converter.converter.world.region.RegionTask;
import me.dags.converter.datagen.Mappings;
import me.dags.converter.util.CopyTask;
import me.dags.converter.util.IO;
import me.dags.converter.util.Threading;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.Version;
import me.dags.converter.version.VersionData;
import me.dags.converter.version.versions.MinecraftVersion;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

public class WorldConverter {

    private final File root;
    private final Level level;
    private final long levelSeed;
    private final CustomData customData;

    public WorldConverter(File root, CustomData customData) {
        this.root = root;
        this.level = new Level(new File(root, "level.dat"));
        this.levelSeed = level.getSeed();
        this.customData = customData;
    }

    private Version getWorldVersion(Version version) throws IOException {
        if (version == MinecraftVersion.DETECT) {
            return level.getVersion();
        }
        return version;
    }

    private VersionData getSourceGameData(Version version) throws Exception {
        File data = new File(root, "game_data.json");
        if (customData.dataIn.get().exists()) {
            data = customData.dataIn.get();
        }
        return level.sync(VersionData.loadGameData(data, version));
    }

    private VersionData getDestGameData(Version from, Version to, VersionData source) throws Exception {
        if (to == from) {
            return source;
        }
        return VersionData.loadGameData(customData.dataOut.get(), to);
    }

    public List<Future<Void>> convert(Version from, Version to, File dest) throws Exception {
        Version sourceVersion = getWorldVersion(from);
        VersionData sourceData = getSourceGameData(sourceVersion);
        VersionData destData = getDestGameData(sourceVersion, to, sourceData);
        Mappings mappings = Mappings.build(sourceData, destData).builtIn();
        ConverterData converterData = ConverterData.create(customData, mappings);
        Converter converter = new ChunkConverter(levelSeed, from, to, converterData);
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
            LevelTask task = new LevelTask(source, dest, context.version, context.versionData);
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
        private final VersionData versionData;
        private final Converter converter;

        private CollectorContext(Version version, VersionData versionData, Converter converter) {
            this.version = version;
            this.versionData = versionData;
            this.converter = converter;
        }
    }
}
