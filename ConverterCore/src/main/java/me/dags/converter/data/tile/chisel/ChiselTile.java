package me.dags.converter.data.tile.chisel;

import me.dags.converter.block.BlockState;
import org.jnbt.CompoundTag;

public interface ChiselTile {

    interface Reader {

        boolean isValid();

        String getTileId();

        BlockState[] getStateArray();
    }

    interface Writer {

        void setTileId(String id);

        void setStateArray(BlockState[] states);

        CompoundTag getRoot();
    }
}
