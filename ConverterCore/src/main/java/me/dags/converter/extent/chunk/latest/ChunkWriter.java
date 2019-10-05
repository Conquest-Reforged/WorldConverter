package me.dags.converter.extent.chunk.latest;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.AbstractChunkWriter;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.version.Version;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.util.List;

public class ChunkWriter extends AbstractChunkWriter {

    public ChunkWriter(Version version) {
        super(version);
    }

    @Override
    protected CompoundTag createRoot() {
        return Nbt.compound();
    }

    @Override
    protected Volume.Writer createSection(int index) {
        return new SectionWriter(index);
    }

    @Override
    protected void addSections(List<CompoundTag> list) {
        list.add(Nbt.compound().put("Y", (byte) -1));
        super.addSections(list);
    }
}
