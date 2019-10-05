package me.dags.converter.extent.io;

import org.jnbt.Tag;

public interface DataWriter extends NBTWriter {

    void setData(String key, Tag<?> data);
}
