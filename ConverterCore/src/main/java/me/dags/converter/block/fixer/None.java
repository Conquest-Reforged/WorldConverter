package me.dags.converter.block.fixer;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.registry.Registry;

public class None implements StateFixer {

    @Override
    public BlockState getActualState(BlockState state, Registry.Parser<BlockState> parser, Chunk.Reader chunk, int x, int y, int z) throws Exception {
        return state;
    }
}
