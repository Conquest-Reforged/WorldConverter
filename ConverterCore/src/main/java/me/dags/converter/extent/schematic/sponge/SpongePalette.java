package me.dags.converter.extent.schematic.sponge;

import me.dags.converter.registry.Registry;
import me.dags.converter.registry.RegistryItem;

public class SpongePalette<T extends RegistryItem<T>> implements Registry.Reader<T> {

    private final T[] values;

    public SpongePalette(T[] values) {
        this.values = values;
    }

    @Override
    public T getValue(int id) {
        return values[id];
    }
}
