package me.dags.converter.extent.chunk.latest;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.latest.AbstractVolumeReader;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

import java.text.ParseException;

public class SectionReader extends AbstractVolumeReader {

    public SectionReader(Registry<BlockState> registry, CompoundTag root) throws ParseException {
        super(registry, root);
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
