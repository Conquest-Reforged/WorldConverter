package me.dags.converter.datagen.mappings;

import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Rule {

    private final String name;
    private final Map<String, String> props;

    public Rule(String name, Map<String, String> props) {
        this.name = name;
        this.props = props;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + props.toString();
    }

    public boolean matches(CompoundTag state) {
        CompoundTag properties = state.getCompound("Properties");
        for (Map.Entry<String, String> e : props.entrySet()) {
            if (e.getValue().startsWith("$")) {
                continue;
            }
            String stateValue = properties.getString(e.getKey());
            if (stateValue.equals(e.getValue())) {
                continue;
            }
            return false;
        }
        return true;
    }

    public Rule apply(Rule other, CompoundTag state) {
        String nameOut = other.name;
        Map<String, String> propsOut = new HashMap<>(other.props);
        for (Map.Entry<String, Tag> e : state.getCompound("Properties")) {
            String key = e.getKey();
            String value = props.get(key);
            String stateValue = e.getValue().asString().getValue();
            if (value == null) {
                propsOut.put(key, stateValue);
                continue;
            }
            if (value.startsWith("$")) {
                if (nameOut.contains(value)) {
                    nameOut = nameOut.replace(value, stateValue);
                }
            }
            for (Map.Entry<String, String> e1 : propsOut.entrySet()) {
                if (e1.getValue().contains(value)) {
                    propsOut.put(e1.getKey(), e1.getValue().replace(value, stateValue));
                }
            }
        }
        return new Rule(nameOut, propsOut);
    }

    public void fill(CompoundTag state) {
        CompoundTag properties = state.getCompound("Properties");

        props.keySet().removeIf(s -> properties.get(s).isAbsent());

        for (Map.Entry<String, Tag> e : properties) {
            if (!props.containsKey(e.getKey())) {
                props.put(e.getKey(), e.getValue().asString().getValue());
            }
        }
    }

    public static Rule parse(String in) {
        int i = in.indexOf('[');
        if (i == -1) {
            return new Rule(in, Collections.emptyMap());
        }
        String name = in.substring(0, i);
        String[] pairs = in.substring(i + 1, in.indexOf(']')).split(",");
        Map<String, String> props = new HashMap<>();
        for (String pair : pairs) {
            String[] kV = pair.split("=");
            props.put(kV[0], kV[1]);
        }
        return new Rule(name, props);
    }
}
