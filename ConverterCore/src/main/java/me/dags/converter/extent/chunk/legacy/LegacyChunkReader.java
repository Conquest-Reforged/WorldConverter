package me.dags.converter.extent.chunk.legacy;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.AbstractChunkReader;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

public class LegacyChunkReader extends AbstractChunkReader {

    private final Registry<BlockState> registry;

    public LegacyChunkReader(Registry<BlockState> registry, CompoundTag root) {
        super(root);
        this.registry = registry;
    }

    @Override
    protected Volume.Reader createSection(CompoundTag section) {
        return new LegacySectionReader(registry, section);
    }
}
