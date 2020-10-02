package me.dags.converter.registry;

import java.util.Iterator;

public class RemappingRegistry<T extends RegistryItem<T>> implements Registry<T> {

    private final Registry<T> registry;
    private final Registry.Mapper<T> mapper;

    public RemappingRegistry(Registry<T> registry, Mapper<T> mapper) {
        this.registry = registry;
        this.mapper = mapper;
    }

    public T getInput(int id) {
        return registry.getValue(id);
    }

    public T getOutput(T input) {
        return mapper.apply(input);
    }

    @Override
    public int size() {
        return registry.size();
    }

    @Override
    public int maxId() {
        return registry.maxId();
    }

    @Override
    public String getVersion() {
        return mapper.getVersion();
    }

    @Override
    public T getValue(int id) {
        return getInput(id);
    }

    @Override
    public int getId(T val) {
        return val.getId();
    }

    @Override
    public Parser<T> getParser() {
        return registry.getParser();
    }

    @Override
    public Iterator<T> iterator() {
        return registry.iterator();
    }
}
