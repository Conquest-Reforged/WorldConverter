package me.dags.converter.version.versions;

import com.google.gson.JsonObject;
import me.dags.converter.version.Version;
import me.dags.converter.version.VersionData;
import me.dags.converter.version.format.BiomeFormat;
import me.dags.converter.version.format.ChunkFormat;
import me.dags.converter.version.format.SchematicFormat;

public enum MinecraftVersion implements Version {
    DETECT(new Auto()),
    V1_10(new V1_10()),
    V1_12(new V1_12()),
    V1_14(new V1_14()),
    V1_15(new V1_15()),
    ;

    private final Version version;

    MinecraftVersion(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version.getVersion();
    }

    @Override
    public int getId() {
        return version.getId();
    }

    @Override
    public String getVersion() {
        return version.getVersion();
    }

    @Override
    public boolean isLegacy() {
        return version.isLegacy();
    }

    @Override
    public ChunkFormat getChunkFormat() {
        return version.getChunkFormat();
    }

    @Override
    public BiomeFormat getBiomeFormat() {
        return version.getBiomeFormat();
    }

    @Override
    public SchematicFormat getSchematicFormat() {
        return version.getSchematicFormat();
    }

    @Override
    public VersionData parseGameData(JsonObject json) throws Exception {
        return version.parseGameData(json);
    }

    public static Version parse(String value) {
        for (Version v : values()) {
            if (value.startsWith(v.getVersion())) {
                return v;
            }
        }
        return MinecraftVersion.DETECT;
    }
}
