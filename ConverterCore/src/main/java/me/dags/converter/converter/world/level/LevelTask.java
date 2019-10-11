package me.dags.converter.converter.world.level;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.data.GameData;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.MinecraftVersion;
import me.dags.converter.version.Version;
import org.jnbt.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelTask implements Callable<Void> {

    private final File source;
    private final File dest;
    private final Version version;
    private final GameData gameData;

    public LevelTask(File source, File dest, Version version, GameData gameData) {
        this.source = source;
        this.dest = dest;
        this.version = version;
        this.gameData = gameData;
    }

    @Override
    public Void call() throws Exception {
        try (InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(source)))) {
            IO.makeFile(dest);
            CompoundTag data = Nbt.read(in).getTag().asCompound();
            CompoundTag level = Nbt.compound();
            level.put("Data", getData(data.getCompound("Data")));
            level.put("fml", getFml(data.getCompound("fml")));
            level.put("forge", data.getCompound("forge"));
            try (OutputStream out = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(dest)))) {
                Nbt.write(level, out);
            }
        } catch (Throwable t) {
            Logger.log(t);
        }
        return null;
    }

    private CompoundTag getData(CompoundTag data) {
        CompoundTag out = Nbt.compound();
        for (Map.Entry<String, Tag> entry : data.getBacking().entrySet()) {
            if (entry.getKey().equals("Version")) {
                out.put(entry.getKey(), getVersion());
            } else {
                out.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

    private CompoundTag getFml(CompoundTag in) {
        CompoundTag registries = Nbt.compound();
        for (Map.Entry<String, Tag> entry : in.get("Registries").asCompound().getBacking().entrySet()) {
            if (entry.getKey().equals("minecraft:biome")) {
                CompoundTag biome = entry.getValue().asCompound().copy();
                biome.put("ids", getBiomes());
                registries.put(entry.getKey(), biome);
            } else if (entry.getKey().equals("minecraft:block")) {
                CompoundTag block = entry.getValue().asCompound().copy();
                block.put("ids", getBlocks());
                registries.put(entry.getKey(), block);
            } else {
                registries.put(entry.getKey(), entry.getValue());
            }
        }

        CompoundTag fml = Nbt.compound();
        fml.put("Registries", registries);
        fml.put("LoadingModList", in.get("LoadingModList"));

        return fml;
    }

    private CompoundTag getVersion() {
        return Nbt.compound(3)
                .put("Id", version.getId())
                .put("Name", version.getVersion())
                .put("Snapshot", 0);
    }

    private ListTag<CompoundTag> getBlocks() {
        if (gameData.blocks.getVersion().equals(MinecraftVersion.V1_12.getVersion())) {
            String[] blocks = new String[4096];
            for (BlockState state : gameData.blocks) {
                String name = state.getBlockName();
                int stateId = gameData.blocks.getId(state);
                int blockId = BlockState.getBlockId(stateId);
                blocks[blockId] = name;
            }
            List<CompoundTag> list = new LinkedList<>();
            for (int i = 0; i < blocks.length; i++) {
                String name = blocks[i];
                if (name != null) {
                    list.add(Nbt.compound().put("K", name).put("V", i));
                }
            }
            return Nbt.list(TagType.COMPOUND, list);
        } else {
            int id = 0;
            Set<String> visited = new HashSet<>();
            List<CompoundTag> list = new LinkedList<>();
            for (BlockState state : gameData.blocks) {
                if (visited.add(state.getBlockName())) {
                    list.add(Nbt.compound().put("K", state.getBlockName()).put("V", id++));
                }
            }
            return Nbt.list(TagType.COMPOUND, list);
        }
    }

    private ListTag<CompoundTag> getBiomes() {
        List<CompoundTag> list = new LinkedList<>();
        for (Biome biome : gameData.biomes) {
            list.add(Nbt.compound(2).put("K", biome.getIdentifier()).put("V", biome.getId()));
        }
        return Nbt.list(TagType.COMPOUND, list);
    }
}
