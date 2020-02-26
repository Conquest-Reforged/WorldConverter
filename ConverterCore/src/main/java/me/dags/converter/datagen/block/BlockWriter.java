package me.dags.converter.datagen.block;

import me.dags.converter.datagen.writer.DataWriter;
import me.dags.converter.datagen.writer.ValueWriter;

import java.io.IOException;

public class BlockWriter implements ValueWriter<BlockData> {
    @Override
    public void write(BlockData block, DataWriter writer) throws IOException {
        writer.name(block.getName()).beginObject();
        {
            if (hasState(block)) {
                writer.name("default").value(block.getDefaultState().getName());
                writer.name("states").beginArray();
                {
                    for (StateData state : block.getStates()) {
                        if (state.getName().isEmpty()) {
                            continue;
                        }
                        writer.value(state.getName());
                    }
                }
                writer.endArray();
            }
        }
        writer.endObject();
    }

    static boolean hasState(BlockData block) {
        return !block.getDefaultState().getName().isEmpty();
    }
}
