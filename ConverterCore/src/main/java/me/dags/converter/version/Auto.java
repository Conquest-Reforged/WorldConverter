package me.dags.converter.version;

import com.google.gson.JsonObject;
import me.dags.converter.block.BlockState;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

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
    public Chunk.Reader chunkReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return null;
    }

    @Override
    public Volume.Reader schematicReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return null;
    }

    @Override
    public Chunk.Writer chunkWriter(WriterConfig config) {
        return null;
    }

    @Override
    public Volume.Writer schematicWriter(WriterConfig config) {
        return null;
    }

    @Override
    public GameData parseGameData(JsonObject json) throws Exception {
        return null;
    }
}
