package me.dags.converter.datagen.writer;

import java.io.IOException;

public interface DataWriter extends AutoCloseable {

    DataWriter name(String name) throws IOException;

    DataWriter value(int value) throws IOException;

    DataWriter value(boolean value) throws IOException;

    DataWriter value(String value) throws IOException;

    DataWriter beginObject() throws IOException;

    DataWriter endObject() throws IOException;

    DataWriter beginArray() throws IOException;

    DataWriter endArray() throws IOException;
}
