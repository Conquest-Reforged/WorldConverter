package me.dags.converter.extent.io;

import me.dags.converter.block.BlockState;

public interface BlockWriter extends NBTWriter {

    void setState(int x, int y, int z, BlockState state);
}
