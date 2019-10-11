package me.dags.converter.extent;

import java.util.HashMap;

public class WriterConfig extends HashMap<String, Object> {

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object o = super.get(key);
        if (o == null) {
            return null;
        }
        return (T) o;
    }
}
