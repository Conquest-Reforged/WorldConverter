package me.dags.converter.extent.chunk.legacy;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.legacy.AbstractLegacyVolumeWriter;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.nibble.ChunkNibbleArray;

public class LegacySectionWriter extends AbstractLegacyVolumeWriter {

    protected LegacySectionWriter(Registry<BlockState> registry, int id) {
        super(registry, 16, 16, 16, ChunkNibbleArray.SIZE, ChunkNibbleArray.FACTORY);
        root.put("Y", (byte) id);
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
