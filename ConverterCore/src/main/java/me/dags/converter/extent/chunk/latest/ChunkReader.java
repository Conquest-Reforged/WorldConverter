package me.dags.converter.extent.chunk.latest;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.AbstractChunkReader;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

public class ChunkReader extends AbstractChunkReader {

    private final Registry<BlockState> registry;

    public ChunkReader(Registry<BlockState> registry, CompoundTag root) {
        super(root);
        this.registry = registry;
    }

    @Override
    public Volume.Reader getSection(int index) throws Exception {
        return super.getSection(index + 1); // Sections start at Y: -1
    }

    @Override
    public int getSectionCount() {
        return super.getSectionCount() - 1; // Sections start at Y: -1
    }

    @Override
    protected Volume.Reader createSection(CompoundTag section) throws Exception {
        return new SectionReader(registry, section);
    }
}
