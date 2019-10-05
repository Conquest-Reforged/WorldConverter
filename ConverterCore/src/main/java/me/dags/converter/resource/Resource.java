package me.dags.converter.resource;

import java.io.IOException;
import java.io.InputStream;

public interface Resource extends Comparable<Resource> {

    String getPath();

    InputStream getInputStream() throws IOException;

    default boolean isPresent() {
        return true;
    }

    @Override
    default int compareTo(Resource o) {
        return getPath().compareTo(o.getPath());
    }

    Resource NULL = new Resource() {
        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public String getPath() {
            return "null";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }
    };
}
