package me.dags.converter.block;

import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Map;

public class Serializer {

    public static String serialize(CompoundTag tag) {
        String name = tag.getString("Name");
        CompoundTag props = tag.getCompound("Properties");
        if (props.isPresent()) {
            int size = name.length() + props.getBacking().size() * 7 * 2;
            StringBuilder sb = new StringBuilder(size);
            sb.append(name).append('[');
            final int len = sb.length();
            props.getBacking().entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(entry -> {
                        if (sb.length() > len) {
                            sb.append(',');
                        }
                        sb.append(entry.getKey()).append('=').append(entry.getValue().asString().getValue());
                    });
            sb.append(']');
            return sb.toString();
        }
        return name;
    }

    public static CompoundTag deserialize(String identifier) throws ParseException {
        int propsStart = identifier.indexOf('[');
        if (propsStart > 0) {
            int propsEnd = identifier.lastIndexOf(']');
            if (propsEnd < 0) {
                throw new ParseException(identifier, identifier.length());
            }
            String name = identifier.substring(0, propsStart);
            String props = identifier.substring(propsStart + 1, propsEnd);
            return deserialize(name, props);
        }
        return deserialize(identifier, "");
    }

    public static CompoundTag deserialize(String name, String properties) throws ParseException {
        int len = properties.length();
        if (len == 0) {
            return simple(name);
        }
        return Nbt.compound(2).put("Name", name).put("Properties", deserializeProps(properties));
    }

    public static CompoundTag deserializeProps(String properties) throws ParseException {
        CompoundTag props = Nbt.compound(5);
        for (int i = 0; i < properties.length(); i++) {
            int keyStart = i;
            int keyEnd = properties.indexOf('=', keyStart);
            if (keyEnd == -1) {
                throw new ParseException(properties, keyStart);
            }

            int valStart = keyEnd + 1;
            if (valStart >= properties.length()) {
                throw new ParseException(properties, valStart);
            }

            int valEnd = properties.indexOf(',', valStart);
            if (valEnd == -1) {
                valEnd = properties.length();
            }

            String key = properties.substring(keyStart, keyEnd);
            String val = properties.substring(valStart, valEnd);

            // # denotes a extended property
            if (key.charAt(0) != '#') {
                props.put(key, val);
            }

            i = valEnd;
        }
        return props;
    }

    public static CompoundTag deserializeExtendedProps(CompoundTag propertiesOutIn, String properties) throws ParseException {
        CompoundTag propertiesOut = propertiesOutIn.copy();
        for (int i = 0; i < properties.length(); i++) {
            int keyStart = properties.indexOf('#', i);
            if (keyStart == -1) {
                break;
            }

            int keyEnd = properties.indexOf('=', keyStart);
            if (keyEnd == -1) {
                throw new ParseException(properties, keyStart);
            }

            int valStart = keyEnd + 1;
            if (valStart >= properties.length()) {
                throw new ParseException(properties, valStart);
            }

            int valEnd = properties.indexOf(',', valStart);
            if (valEnd == -1) {
                valEnd = properties.lastIndexOf(']');
                if (valEnd == -1) {
                    valEnd = properties.length();
                }
            }

            String key = properties.substring(keyStart, keyEnd);
            String val = properties.substring(valStart, valEnd);

            propertiesOut.put(key, val);
        }
        return propertiesOut;
    }

    private static CompoundTag simple(String name) {
        return Nbt.compound(1).put("Name", name);
    }
}
