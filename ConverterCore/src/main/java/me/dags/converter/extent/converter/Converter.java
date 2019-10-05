package me.dags.converter.extent.converter;

import org.jnbt.CompoundTag;

public interface Converter {

    CompoundTag convert(CompoundTag in) throws Exception;
}
