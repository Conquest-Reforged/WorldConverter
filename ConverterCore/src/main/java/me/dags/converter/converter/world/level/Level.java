package me.dags.converter.converter.world.level;

import me.dags.converter.biome.Biome;
import me.dags.converter.biome.registry.BiomeRegistry;
import me.dags.converter.block.BlockState;
import me.dags.converter.block.registry.BlockRegistry;
import me.dags.converter.data.GameData;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.MinecraftVersion;
import me.dags.converter.version.Version;
import org.jnbt.*;

import java.io.*;
import java.util.*;
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

    public GameData sync(GameData from) throws Exception {
        Logger.log("Synchronising GameData ids with level.dat");
        try (InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            CompoundTag root = Nbt.read(in).getTag().asCompound();
            if (root.isAbsent()) {
                throw new IOException("Unable to read Level file");
            }

            Registry<Biome> biomes = syncBiomes(from.biomes, loadMappings(root, "minecraft:biome"));
            Registry<BlockState> blocks = from.blocks;
            if (from.version.getId() <= MinecraftVersion.V1_12.getId()) {
                blocks = syncBlocks(from.blocks, loadMappings(root, "minecraft:block"));
            }

            return new GameData(from.version, blocks, biomes);
        }
    }

    private Registry<BlockState> syncBlocks(Registry<BlockState> registry, Map<String, Integer> mappings) throws Exception {
        BlockRegistry.Builder<BlockState> builder = BlockRegistry.builder(registry.getVersion());
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
                int stateId = registry.getId(state);
                int blockId = BlockState.getBlockId(stateId);
                if (blockId != id) {
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
        ListTag<CompoundTag> mappings = level.get("fml", "Registries", registry).asCompound().getListTag("ids", TagType.COMPOUND);
        if (mappings.isAbsent()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> map = new HashMap<>();
        for (Tag pair : mappings) {
            CompoundTag entry = pair.asCompound();
            map.put(entry.getString("K"), entry.getInt("V"));
        }
        return map;
    }
}
