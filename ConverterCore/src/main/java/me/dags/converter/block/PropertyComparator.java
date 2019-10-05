package me.dags.converter.block;

import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Map;

public class PropertyComparator implements Comparator<CompoundTag> {

    private final CompoundTag defaults;

    public PropertyComparator(String properties) throws ParseException {
        this(Serializer.deserializeProps(properties));
    }

    public PropertyComparator(CompoundTag defaults) {
        this.defaults = defaults;
    }

    @Override
    public int compare(CompoundTag a, CompoundTag b) {
        int score = 0;
        for (Map.Entry<String, Tag> prop : a.getBacking().entrySet()) {
            Object aVal = prop.getValue().getValue();
            Object bVal = b.get(prop.getKey()).getValue();
            if (aVal.equals(bVal)) {
                continue;
            }
            Object defVal = defaults.get(prop.getKey()).getValue();
            if (aVal.equals(defVal)) {
                score--;
            } else if (bVal.equals(defVal)) {
                score++;
            }
        }
        return score;
    }
}
