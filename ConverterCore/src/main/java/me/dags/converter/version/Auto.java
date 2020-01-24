package me.dags.converter.version;

import com.google.gson.JsonObject;
import me.dags.converter.data.GameData;
import me.dags.converter.version.format.BiomeFormat;
import me.dags.converter.version.format.ChunkFormat;
import me.dags.converter.version.format.SchematicFormat;

public class Auto implements Version {

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getVersion() {
        return "auto";
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public ChunkFormat getChunkFormat() {
        return null;
    }

    @Override
    public BiomeFormat getBiomeFormat() {
        return null;
    }

    @Override
    public SchematicFormat getSchematicFormat() {
        return null;
    }

    @Override
    public GameData parseGameData(JsonObject json) throws Exception {
        return null;
    }
}
