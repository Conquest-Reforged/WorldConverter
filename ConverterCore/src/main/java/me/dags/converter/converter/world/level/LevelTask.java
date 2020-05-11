package me.dags.converter.converter.world.level;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.Version;
import me.dags.converter.version.VersionData;
import me.dags.converter.version.versions.MinecraftVersion;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelTask implements Callable<Void> {

    private final File source;
    private final File dest;
    private final Version version;
    private final VersionData versionData;

    public LevelTask(File source, File dest, Version version, VersionData versionData) {
        this.source = source;
        this.dest = dest;
        this.version = version;
        this.versionData = versionData;
    }

    @Override
    public Void call() throws Exception {
        try (InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(source)))) {
            IO.makeFile(dest);
            CompoundTag data = Nbt.read(in).getTag().asCompound();
            CompoundTag level = Nbt.compound();
            level.put("Data", getData(data.getCompound("Data")));
            level.put("FML", getFml(data.getCompound("FML")));
            level.put("Forge", data.getCompound("Forge"));
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
        CompoundTag registriesIn = in.getCompound("Registries");
        CompoundTag registriesOut = Nbt.compound();

        registriesIn.forEach(entry -> {
            if (entry.getKey().equals("minecraft:biomes")) {
                CompoundTag biome = entry.getValue().asCompound().copy();
                biome.put("ids", getBiomes());
                registriesOut.put(entry.getKey(), biome);
            } else if (entry.getKey().equals("minecraft:blocks")) {
                CompoundTag block = entry.getValue().asCompound().copy();
                block.put("ids", getBlocks());
                registriesOut.put(entry.getKey(), block);
            } else {
                registriesOut.put(entry.getKey(), entry.getValue());
            }
        });

        CompoundTag fml = Nbt.compound();
        fml.put("Registries", registriesOut);
        fml.put("ModList", in.get("ModList"));

        return fml;
    }

    private CompoundTag getVersion() {
        return Nbt.compound(3)
                .put("Id", version.getId())
                .put("Name", version.getVersion())
                .put("Snapshot", 0);
    }

    private ListTag<CompoundTag> getBlocks() {
        if (versionData.blocks.getVersion().equals(MinecraftVersion.V1_12.getVersion())) {
            String[] blocks = new String[4096];
            for (BlockState state : versionData.blocks) {
                String name = state.getBlockName();
                int stateId = versionData.blocks.getId(state);
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
            for (BlockState state : versionData.blocks) {
                if (visited.add(state.getBlockName())) {
                    list.add(Nbt.compound().put("K", state.getBlockName()).put("V", id++));
                }
            }
            return Nbt.list(TagType.COMPOUND, list);
        }
    }

    private ListTag<CompoundTag> getBiomes() {
        List<CompoundTag> list = new LinkedList<>();
        for (Biome biome : versionData.biomes) {
            list.add(Nbt.compound(2).put("K", biome.getIdentifier()).put("V", biome.getId()));
        }
        return Nbt.list(TagType.COMPOUND, list);
    }
}
