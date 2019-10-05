package me.dags.converter.extent.converter;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.Extent;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

public interface ReaderFunc {

    Extent.Reader get(Registry<BlockState> registry, CompoundTag root) throws Exception;
}
