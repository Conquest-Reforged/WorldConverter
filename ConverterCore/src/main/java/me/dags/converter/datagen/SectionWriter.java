package me.dags.converter.datagen;

import me.dags.converter.datagen.writer.DataWriter;
import me.dags.converter.datagen.writer.ValueWriter;

import java.io.IOException;

public class SectionWriter<T> implements AutoCloseable {

    private final String name;
    private final DataWriter writer;
    private final ValueWriter<T> adapter;

    private boolean open = false;

    public SectionWriter(String name, DataWriter writer, ValueWriter<T> adapter) {
        this.name = name;
        this.writer = writer;
        this.adapter = adapter;
    }

    private void ensureOpen() throws IOException {
        if (!open) {
            open = true;
            writer.name(name).beginObject();
        }
    }

    public void write(T t) throws IOException {
        ensureOpen();
        adapter.write(t, writer);
    }

    @Override
    public void close() throws Exception {
        if (open) {
            writer.endObject();
        }
    }
}
