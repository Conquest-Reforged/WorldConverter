package me.dags.converter.block.registry;

import me.dags.converter.registry.Registry;
import me.dags.converter.registry.RegistryItem;
import me.dags.converter.util.storage.IntMap;

public class PaletteReader<T extends RegistryItem> implements Registry.Reader<T> {

    private final IntMap<T> idToVal;
    private final T fallback;

    public PaletteReader(IntMap<T> idToVal, T fallback) {
        this.idToVal = idToVal;
        this.fallback = fallback;
    }

    @Override
    public T getVal(int id) {
        return idToVal.getOrDefault(id, fallback);
    }
}
