package me.dags.converter.version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.converter.data.GameData;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.format.BiomeFormat;
import me.dags.converter.version.format.ChunkFormat;
import me.dags.converter.version.format.SchematicFormat;
import me.dags.converter.version.format.StructureFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface Version {

    int getId();

    String getVersion();

    boolean isLegacy();

    ChunkFormat getChunkFormat();

    BiomeFormat getBiomeFormat();

    SchematicFormat getSchematicFormat();

    default StructureFormat getStructureFormat() {
        return StructureFormat.INSTANCE;
    }

    GameData parseGameData(JsonObject json) throws Exception;

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
}
