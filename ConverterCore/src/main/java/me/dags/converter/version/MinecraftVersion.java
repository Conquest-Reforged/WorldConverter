package me.dags.converter.version;

import com.google.gson.JsonObject;
import me.dags.converter.block.BlockState;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

public enum MinecraftVersion implements Version {
    DETECT(new Auto()),
    V1_12(new V1_12()),
    V1_14(new V1_14()),
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
    public Chunk.Reader chunkReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return version.chunkReader(registry, root);
    }

    @Override
    public Volume.Reader schematicReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return version.schematicReader(registry, root);
    }

    @Override
    public Extent.Reader structureReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return version.structureReader(registry, root);
    }

    @Override
    public Chunk.Writer chunkWriter(WriterConfig config) {
        return version.chunkWriter(config);
    }

    @Override
    public Volume.Writer schematicWriter(WriterConfig config) {
        return version.schematicWriter(config);
    }

    @Override
    public Extent.Writer structureWriter(WriterConfig config) {
        return version.structureWriter(config);
    }

    @Override
    public GameData parseGameData(JsonObject json) throws Exception {
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
