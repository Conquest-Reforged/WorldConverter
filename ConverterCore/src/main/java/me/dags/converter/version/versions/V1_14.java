package me.dags.converter.version.versions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.converter.biome.Biome;
import me.dags.converter.biome.registry.BiomeRegistry;
import me.dags.converter.block.BlockState;
import me.dags.converter.block.Serializer;
import me.dags.converter.block.registry.BlockRegistry;
import me.dags.converter.datagen.Schema;
import me.dags.converter.datagen.biome.BiomeWriter;
import me.dags.converter.datagen.block.BlockWriter;
import me.dags.converter.version.VersionData;
import me.dags.converter.version.Version;
import me.dags.converter.version.format.BiomeFormat;
import me.dags.converter.version.format.ChunkFormat;
import me.dags.converter.version.format.SchematicFormat;
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
    public ChunkFormat getChunkFormat() {
        return ChunkFormat.LATEST;
    }

    @Override
    public BiomeFormat getBiomeFormat() {
        return BiomeFormat.LEGACY;
    }

    @Override
    public SchematicFormat getSchematicFormat() {
        return SchematicFormat.SPONGE;
    }

    @Override
    public VersionData parseGameData(JsonObject json) throws Exception {
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
                BlockState blockState = new BlockState(++stateId, data, false);
                blocks.add(blockState);
            } else {
                for (JsonElement state : states) {
                    CompoundTag data = Serializer.deserialize(entry.getKey(), state.getAsString());
                    BlockState blockState = new BlockState(++stateId, data, false);
                    blocks.add(blockState);
                }
            }
        }

        BiomeRegistry.Builder<Biome> biomes = BiomeRegistry.builder(getVersion());
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("biomes").entrySet()) {
            Biome biome = new Biome(entry.getKey(), entry.getValue().getAsInt());
            biomes.addUnchecked(biome.getId(), biome);
        }

        return new VersionData(this, blocks.build(), biomes.build());
    }
}
