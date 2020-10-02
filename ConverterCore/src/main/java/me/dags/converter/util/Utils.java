package me.dags.converter.util;

import me.dags.converter.util.map.FastLinkedMap;
import me.dags.converter.util.map.FastMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Utils {

    public static String toIdentifier(String namespace, String name) {
        StringBuilder sb = new StringBuilder(namespace.length() + name.length() + 5);
        sb.append(namespace).append(':');
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
                if (i > 0) {
                    sb.append('_');
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        if (elements.length == 0) {
            return Collections.emptyList();
        }
        if (elements.length == 1) {
            return Collections.singletonList(elements[0]);
        }
        return Collections.unmodifiableList(Arrays.asList(elements));
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1) {
        return Collections.singletonMap(k1, v1);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        Map<K, V> map = Utils.newMap();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V def) {
        V v = map.get(key);
        return v == null ? def : v;
    }

    public static <K, V> Map<K, V> newMap() {
        return new FastMap<>();
    }

    public static <K, V> Map<K, V> newMap(int size) {
        return new FastMap<>(size);
    }

    public static <K, V> Map<K, V> newMap(Map<K, V> other) {
        return new FastMap<>(other);
    }

    public static <K, V> Map<K, V> newOrderedMap() {
        return new FastLinkedMap<>();
    }

    public static <K, V> Map<K, V> newOrderedMap(int size) {
        return new FastLinkedMap<>(size);
    }

    public static <K, V> Map<K, V> newOrderedMap(Map<K, V> other) {
        return new FastLinkedMap<>(other);
    }
}
