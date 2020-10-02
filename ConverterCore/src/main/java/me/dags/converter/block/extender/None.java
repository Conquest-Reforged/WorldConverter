package me.dags.converter.block.extender;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.registry.Registry;

public class None implements StateExtender {
    @Override
    public BlockState getExtendedState(BlockState state, Registry.Parser<BlockState> parser, Chunk.Reader chunk, int x, int y, int z) throws Exception {
        return state;
    }
}
