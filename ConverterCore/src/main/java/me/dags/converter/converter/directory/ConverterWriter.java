package me.dags.converter.converter.directory;

import me.dags.converter.block.BlockState;
import me.dags.converter.converter.ConverterData;
import me.dags.converter.extent.Extent;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

public class ConverterWriter implements Extent.Writer, Extent.Visitor {

    private final Extent.Writer writer;
    private final ConverterData converterData;

    public ConverterWriter(Extent.Writer writer, ConverterData converterData) {
        this.writer = writer;
        this.converterData = converterData;
    }

    @Override
    public void setState(int x, int y, int z, BlockState state) {
        state = converterData.blocks.getOutput(state);
        writer.setState(x, y, z, state);
    }

    @Override
    public void setData(String key, Tag<?> data) {
        writer.setData(key, data);
    }

    @Override
    public CompoundTag flush() {
        return writer.flush();
    }

    @Override
    public void visit(int x, int y, int z, BlockState state) {
        setState(x, y, z, state);
    }
}
