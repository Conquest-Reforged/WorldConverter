package me.dags.converter.data.writer;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class JsonDataWriter implements DataWriter {

    private final JsonWriter writer;

    public JsonDataWriter(JsonWriter writer) {
        this.writer = writer;
        writer.setIndent("  ");
    }

    @Override
    public DataWriter name(String name) throws IOException {
        writer.name(name);
        return this;
    }

    @Override
    public DataWriter value(int value) throws IOException {
        writer.value(value);
        return this;
    }

    @Override
    public DataWriter value(String value) throws IOException {
        writer.value(value);
        return this;
    }

    @Override
    public DataWriter beginObject() throws IOException {
        writer.beginObject();
        return this;
    }

    @Override
    public DataWriter endObject() throws IOException {
        writer.endObject();
        return this;
    }

    @Override
    public DataWriter beginArray() throws IOException {
        writer.beginArray();
        return this;
    }

    @Override
    public DataWriter endArray() throws IOException {
        writer.endArray();
        return this;
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
