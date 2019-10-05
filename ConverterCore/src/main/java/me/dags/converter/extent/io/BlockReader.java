package me.dags.converter.extent.io;

import me.dags.converter.block.BlockState;

public interface BlockReader {

    BlockState getState(int x, int y, int z);
}
