package me.dags.converter.datagen.writer;

import java.io.IOException;

public interface ValueWriter<T> {

    void write(T t, DataWriter writer) throws IOException;
}
