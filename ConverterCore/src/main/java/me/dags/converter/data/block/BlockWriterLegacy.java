package me.dags.converter.data.block;

import me.dags.converter.data.writer.DataWriter;
import me.dags.converter.data.writer.ValueWriter;

import java.io.IOException;

public class BlockWriterLegacy implements ValueWriter<BlockData> {
    @Override
    public void write(BlockData block, DataWriter writer) throws IOException {
        writer.name(block.getName()).beginObject();
        {
            writer.name("id").value(block.getId());
            if (BlockWriter.hasState(block)) {
                writer.name("default").value(block.getDefaultState().getName());
                writer.name("states").beginObject();
                {
                    for (StateData state : block.getStates()) {
                        writer.name(state.getName()).value(state.getId());
                    }
                }
                writer.endObject();
            }
        }
        writer.endObject();
    }
}
