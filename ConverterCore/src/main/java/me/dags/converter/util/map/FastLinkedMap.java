package me.dags.converter.util.map;

import java.util.LinkedHashMap;
import java.util.Map;

public class FastLinkedMap<K, V> extends LinkedHashMap<K, V> {

    public FastLinkedMap() {}

    public FastLinkedMap(int size) {
        super(size);
    }

    public FastLinkedMap(Map<K, V> other) {
        super(other);
    }
    
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V v = get(key);
        return v == null ? defaultValue : v;
    }
}
