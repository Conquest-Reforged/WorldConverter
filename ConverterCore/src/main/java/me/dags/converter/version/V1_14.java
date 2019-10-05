package me.dags.converter.version;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.converter.biome.Biome;
import me.dags.converter.biome.registry.BiomeRegistry;
import me.dags.converter.block.BlockState;
import me.dags.converter.block.Serializer;
import me.dags.converter.block.registry.BlockRegistry;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.chunk.latest.ChunkReader;
import me.dags.converter.extent.chunk.latest.ChunkWriter;
import me.dags.converter.extent.schematic.latest.SchematicReader;
import me.dags.converter.extent.schematic.latest.SchematicWriter;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

import java.util.Map;

public class V1_14 implements Version {

    @Override
    public int getId() {
        return 1976;
    }

    @Override
    public String getVersion() {
        return "1.14";
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public Chunk.Reader chunkReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return new ChunkReader(registry, root);
    }

    @Override
    public Chunk.Writer chunkWriter(WriterConfig config) {
        return new ChunkWriter(this);
    }

    @Override
    public Volume.Reader schematicReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return new SchematicReader(registry, root);
    }

    @Override
    public Volume.Writer schematicWriter(WriterConfig config) {
        return new SchematicWriter(config);
    }

    @Override
    public GameData parseGameData(JsonObject json) throws Exception {
        int stateId = 0;
        BlockRegistry.Builder<BlockState> blocks = BlockRegistry.builder(getVersion());
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("blocks").entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject block = entry.getValue().getAsJsonObject();
            JsonArray states = block.getAsJsonArray("states");
            if (states == null) {
                CompoundTag data = Serializer.deserialize(entry.getKey());
                BlockState blockState = new BlockState(++stateId, data);
                blocks.add(blockState);
            } else {
                for (JsonElement state : states) {
                    CompoundTag data = Serializer.deserialize(entry.getKey(), state.getAsString());
                    BlockState blockState = new BlockState(++stateId, data);
                    blocks.add(blockState);
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
