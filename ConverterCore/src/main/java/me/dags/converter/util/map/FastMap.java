package me.dags.converter.util.map;

import java.util.HashMap;
import java.util.Map;

public class FastMap<K, V> extends HashMap<K, V> {
    
    public FastMap() {}
    
    public FastMap(int size) {
        super(size);
    }

    public FastMap(Map<K, V> other) {
        super(other);
    }
    
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V v = get(key);
        return v == null ? defaultValue : v;
    }
}
