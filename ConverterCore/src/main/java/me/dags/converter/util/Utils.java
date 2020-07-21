package me.dags.converter.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
}
