package me.dags.converter.converter;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.Extent;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

public interface ReaderFactory {

    Extent.Reader create(Registry<BlockState> registry, CompoundTag root) throws Exception;
}
