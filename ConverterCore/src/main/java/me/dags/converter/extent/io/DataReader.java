package me.dags.converter.extent.io;

import org.jnbt.Tag;

public interface DataReader {

    Tag<?> getData(String key);
}
