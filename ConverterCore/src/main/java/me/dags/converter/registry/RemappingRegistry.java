package me.dags.converter.registry;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;

import java.util.Iterator;

public class RemappingRegistry<T extends RegistryItem> implements Registry<T> {

    private final Registry<T> registry;
    private final Registry.Mapper<T> mapper;

    public RemappingRegistry(Registry<T> registry, Mapper<T> mapper) {
        this.registry = registry;
        this.mapper = mapper;
    }

    @Override
    public int size() {
        return registry.size();
    }

    @Override
    public String getVersion() {
        return mapper.getVersion();
    }

    @Override
    public T getValue(int id) {
        return getOutput(getInput(id));
    }

    @Override
    public T getInput(int id) {
        T source = registry.getValue(id);
        if (registry.isDefault(source)) {
            int block = BlockState.getBlockId(id);
            int meta = BlockState.getMetaData(id);
            new RuntimeException("Missing value for id: " + id + "(" + block + ":" + meta + ")").printStackTrace();
            System.exit(1);
        }
        return source;
    }

    @Override
    public T getOutput(T input) {
        T result = mapper.apply(input);
        if (!(result instanceof Biome) && result == input) {
            new RuntimeException("Unable to remap value: " + input).printStackTrace();
            System.out.println(1);
        }
        return result;
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
