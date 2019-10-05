package me.dags.converter.block.registry;

import me.dags.converter.registry.Registry;
import me.dags.converter.registry.RegistryItem;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaletteWriter<T extends RegistryItem> implements Registry.Writer<T> {

    private final Map<T, Integer> palette = new LinkedHashMap<>(16);

    @Override
    public int getOrCreateId(T val) {
        return palette.computeIfAbsent(val, t -> palette.size());
    }

    @Override
    public int size() {
        return palette.size();
    }

    @Override
    public Iterator<T> iterator() {
        return palette.keySet().iterator();
    }
}
