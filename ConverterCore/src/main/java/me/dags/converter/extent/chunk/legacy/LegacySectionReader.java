package me.dags.converter.extent.chunk.legacy;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.legacy.AbstractLegacyVolumeReader;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.nibble.ChunkNibbleArray;
import org.jnbt.CompoundTag;

public class LegacySectionReader extends AbstractLegacyVolumeReader {

    public LegacySectionReader(Registry<BlockState> registry, CompoundTag section) {
        super(registry, section, ChunkNibbleArray.FACTORY);
    }

    @Override
    public int indexOf(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getLength() {
        return 16;
    }
}
