package me.dags.converter.extent.chunk.latest;

import me.dags.converter.extent.volume.latest.AbstractVolumeWriter;

public class SectionWriter extends AbstractVolumeWriter {

    public SectionWriter(int id) {
        super(16, 16, 16);
        root.put("Y", (byte) id);
    }

    @Override
    public int indexOf(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
    }
}
