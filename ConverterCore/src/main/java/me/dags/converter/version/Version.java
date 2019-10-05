package me.dags.converter.version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.structure.StructureReader;
import me.dags.converter.extent.structure.StructureWriter;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import org.jnbt.CompoundTag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface Version {

    int getId();

    String getVersion();

    boolean isLegacy();

    // READERS

    Chunk.Reader chunkReader(Registry<BlockState> registry, CompoundTag root) throws Exception;

    Volume.Reader schematicReader(Registry<BlockState> registry, CompoundTag root) throws Exception;

    default Extent.Reader structureReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return new StructureReader(registry, root);
    }

    // WRITERS

    Chunk.Writer chunkWriter(WriterConfig config);

    Volume.Writer schematicWriter(WriterConfig config);

    default Extent.Writer structureWriter(WriterConfig config) {
        return new StructureWriter(config);
    }

    default JsonObject loadGameDataJson() throws Exception {
        String path = String.format("/data/%s/game_data.json", getVersion());
        try (InputStream inputStream = Version.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new FileNotFoundException(path);
            }
            JsonElement root = IO.loadJson(inputStream);
            if (!root.isJsonObject()) {
                throw new IOException("Game data could not be read from json!");
            }
            return root.getAsJsonObject();
        }
    }

    default GameData loadGameData() throws Exception {
        Logger.log("Loading game data for version ", this);
        return parseGameData(loadGameDataJson());
    }

    GameData parseGameData(JsonObject json) throws Exception;
}
