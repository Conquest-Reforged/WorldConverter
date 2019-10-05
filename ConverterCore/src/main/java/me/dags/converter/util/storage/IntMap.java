package me.dags.converter.util.storage;

import java.util.Iterator;
import java.util.function.Function;

public class IntMap<T> implements Iterable<T> {

    private final Object[] values;
    private int size = 0;

    public IntMap(int size) {
        this.values = new Object[size];
    }

    public int size() {
        return size;
    }

    public boolean containsKey(int key) {
        if (key < values.length) {
            return values[key] != null;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public T get(int key) {
        if (key < values.length) {
            Object value = values[key];
            if (value != null) {
                return (T) value;
            }
        }
        return null;
    }

    public T getOrDefault(int key, T defValue) {
        T t = get(key);
        if (t == null) {
            return defValue;
        }
        return t;
    }

    public T computeIfAbsent(int key, Function<Integer, T> function) {
        T t = get(key);
        if (t == null) {
            t = function.apply(key);
            put(key, t);
        }
        return t;
    }

    public void put(int key, T value) {
        values[key] = value;
        size = Math.max(size, key + 1);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int index = -1;

            @Override
            public boolean hasNext() {
                return index + 1 < size();
            }

            @Override
            public T next() {
                T t = get(++index);
                if (t != null) {
                    return t;
                }
                if (!hasNext()) {
                    throw new RuntimeException("Out of bounds");
                }
                return next();
            }
        };
    }
}
