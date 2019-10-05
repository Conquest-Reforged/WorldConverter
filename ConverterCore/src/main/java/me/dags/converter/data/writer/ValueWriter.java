package me.dags.converter.data.writer;

import java.io.IOException;

public interface ValueWriter<T> {

    void write(T t, DataWriter writer) throws IOException;
}
