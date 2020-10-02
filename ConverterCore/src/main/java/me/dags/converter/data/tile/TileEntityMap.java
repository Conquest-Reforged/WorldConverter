package me.dags.converter.data.tile;

import org.jnbt.CompoundTag;

public interface TileEntityMap {

    CompoundTag getEntity(int x, int y, int z);
}
