package me.dags.converter.version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.converter.biome.Biome;
import me.dags.converter.biome.registry.BiomeRegistry;
import me.dags.converter.block.BlockState;
import me.dags.converter.block.PropertyComparator;
import me.dags.converter.block.Serializer;
import me.dags.converter.block.registry.BlockRegistry;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.chunk.legacy.LegacyChunkReader;
import me.dags.converter.extent.chunk.legacy.LegacyChunkWriter;
import me.dags.converter.extent.schematic.legacy.LegacySchematicReader;
import me.dags.converter.extent.schematic.legacy.LegacySchematicWriter;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.util.Map;

public class V1_12 implements Version {

    @Override
    public int getId() {
        return 1343;
    }

    @Override
    public String getVersion() {
        return "1.12";
    }

    @Override
    public boolean isLegacy() {
        return true;
    }

    @Override
    public Chunk.Reader chunkReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return new LegacyChunkReader(registry, root);
    }

    @Override
    public Chunk.Writer chunkWriter(WriterConfig config) {
        return new LegacyChunkWriter(this, config);
    }

    @Override
    public Volume.Reader schematicReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return new LegacySchematicReader(registry, root);
    }

    @Override
    public Volume.Writer schematicWriter(WriterConfig config) {
        return new LegacySchematicWriter(config);
    }

    @Override
    public GameData parseGameData(JsonObject json) throws Exception {
        BlockRegistry.Builder<BlockState> blocks = BlockRegistry.builder(getVersion());
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("blocks").entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }

            JsonObject block = entry.getValue().getAsJsonObject();
            boolean upgrade = block.get("upgrade").getAsBoolean();
            JsonObject states = block.getAsJsonObject("states");

            if (states == null || states.size() == 0) {
                int blockId = block.get("id").getAsInt();
                int stateId = BlockState.getStateId(blockId, 0);
                CompoundTag state = Nbt.compound(1).put("Name", entry.getKey());
                blocks.addUnchecked(stateId, new BlockState(stateId, upgrade, state));
            } else {
                int blockId = block.get("id").getAsInt();
                String defaults = block.get("default").getAsString();
                CompoundTag defProps = Serializer.deserializeProps(defaults);
                PropertyComparator stateComparator = new PropertyComparator(defProps);

                int minId = 15;
                CompoundTag[] persistentStates = new CompoundTag[16];
                for (Map.Entry<String, JsonElement> state : states.entrySet()) {
                    int meta = state.getValue().getAsInt();
                    minId = Math.min(minId, meta);
                    CompoundTag props = Serializer.deserializeProps(state.getKey());
                    CompoundTag current = persistentStates[meta];
                    if (current == null) {
                        persistentStates[meta] = props;
                        continue;
                    }
                    if (stateComparator.compare(current, props) > 0) {
                        persistentStates[meta] = props;
                    }
                }

                for (int meta = 0; meta < persistentStates.length; meta++) {
                    CompoundTag props = persistentStates[meta];
                    if (meta < minId) {
//                        props = defProps;
                    }

                    if (props == null) {
                        continue;
                    }

                    int stateId = BlockState.getStateId(blockId, meta);
                    CompoundTag state = Nbt.compound(2)
                            .put("Name", entry.getKey())
                            .put("Properties", props);

                    blocks.addUnchecked(stateId, new BlockState(stateId, upgrade, state));
                }
            }
        }

        BiomeRegistry.Builder<Biome> biomes = BiomeRegistry.builder(getVersion());
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("biomes").entrySet()) {
            Biome biome = new Biome(entry.getKey(), entry.getValue().getAsInt());
            biomes.addUnchecked(biome.getId(), biome);
        }

        return new GameData(this, blocks.build(), biomes.build());
    }
}
