package me.dags.converter.converter.world.level;

import me.dags.converter.biome.Biome;
import me.dags.converter.biome.registry.BiomeRegistry;
import me.dags.converter.block.BlockState;
import me.dags.converter.block.registry.BlockRegistry;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.Version;
import me.dags.converter.version.VersionData;
import me.dags.converter.version.versions.MinecraftVersion;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class Level {

    private final File file;

    public Level(File file) {
        this.file = file;
    }

    public Version getVersion() throws IOException {
        try (InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            CompoundTag root = Nbt.read(in).getTag().asCompound();
            if (root.isAbsent()) {
                throw new IOException("Unable to read Level file");
            }
            CompoundTag version = root.get("Data", "Version").asCompound();
            if (version.isAbsent()) {
                throw new IOException("Level file missing version information");
            }
            String name = version.getString("Name");
            for (Version v : MinecraftVersion.values()) {
                if (name.startsWith(v.getVersion())) {
                    return v;
                }
            }
            throw new IOException("Unsupported version: " + name);
        }
    }

    public long getSeed() {
        try (InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            CompoundTag root = Nbt.read(in).getTag().asCompound();
            if (root.isAbsent()) {
                throw new IOException("Unable to read Level file");
            }

            LongTag seed = root.get("Data", "RandomSeed").asLong();
            if (seed.isAbsent()) {
                return 0L;
            }

            return seed.getValue();
        } catch (IOException e) {
            Logger.log(e).flush();
            return 0L;
        }
    }

    public VersionData sync(VersionData from) throws Exception {
        Logger.log("Synchronising GameData ids with level.dat");
        try (InputStream in = new GZIPInputStream(new FileInputStream(file))) {
            CompoundTag root = Nbt.read(in).getTag().asCompound();
            if (root.isAbsent()) {
                throw new IOException("Unable to read Level file");
            }

            Registry<BlockState> blocks = from.blocks;
            if (from.version.getId() <= MinecraftVersion.V1_12.getId()) {
                blocks = syncBlocks(from.blocks, loadMappings(root, "minecraft:blocks"));
            }

            Registry<Biome> biomes = syncBiomes(from.biomes, loadMappings(root, "minecraft:biomes"));

            return new VersionData(from.version, blocks, biomes);
        }
    }

    private Registry<BlockState> syncBlocks(Registry<BlockState> registry, Map<String, Integer> mappings) throws Exception {
        BlockRegistry.Builder<BlockState> builder = BlockRegistry.builder(registry.getVersion(), getMaxId(registry, mappings));
        Set<String> visited = new HashSet<>();

        String version = registry.getVersion();
        for (BlockState state : registry) {
            String blockName = state.getBlockName();
            Integer id = mappings.get(blockName);
            if (id == null) {
                builder.add(registry.getId(state), state);
                Logger.log("GameData for version:", version, "contains block:", blockName, "but the provided level.dat does not");
            } else {
                visited.add(blockName);
                int stateId = state.getId();
                int blockId = BlockState.getBlockId(stateId);
                if (blockId != id.intValue()) {
                    int metadata = BlockState.getMetaData(stateId);
                    builder.add(BlockState.getStateId(id, metadata), state);
                    Logger.logf("Remapping block id for: %s [%s:%s -> %s:%s]", blockName, blockId, metadata, id, metadata);
                } else {
                    builder.add(stateId, state);
                }
            }
        }

        for (String name : mappings.keySet()) {
            if (!visited.contains(name)) {
                Logger.log("Level.dat contains block:", name, "but the provided GameData does not");
            }
        }

        return builder.build();
    }

    private Registry<Biome> syncBiomes(Registry<Biome> registry, Map<String, Integer> mappings) throws Exception {
        BiomeRegistry.Builder<Biome> builder = BiomeRegistry.builder(registry.getVersion());
        String version = registry.getVersion();

        for (Biome biome : registry) {
            String name = biome.getIdentifier();
            Integer id = mappings.get(name);
            if (id == null) {
                builder.add(biome.getId(), biome);
                Logger.log("GameData for version:", version, "contains biome:", name, "but the provided level.dat does not");
            } else {
                int biomeId = registry.getId(biome);
                if (biomeId != id) {
                    builder.add(id, new Biome(biome.getIdentifier(), id));
                    Logger.logf("Remapping biome id for: %s [%s -> %s]", biome, biome, id);
                } else {
                    builder.add(biomeId, biome);
                }
            }
        }
        return builder.build();
    }

    private Map<String, Integer> loadMappings(CompoundTag level, String registry) {
        CompoundTag reg = level.getCompound("FML").getCompound("Registries").getCompound(registry);
        ListTag<CompoundTag> mappings = reg.getListTag("ids", TagType.COMPOUND);
        if (mappings.isAbsent()) {
            Logger.log("No mappings found for registry", registry);
            return Collections.emptyMap();
        }
        Map<String, Integer> map = new HashMap<>();
        for (Tag<?> pair : mappings) {
            CompoundTag entry = pair.asCompound();
            map.put(entry.getString("K").trim(), entry.getInt("V"));
        }
        return map;
    }

    private static int getMaxId(Registry<?> registry, Map<?, Integer> map) {
        return Math.max(registry.size(), map.values().stream().max(Integer::compareTo).orElse(map.size()));
    }
}
