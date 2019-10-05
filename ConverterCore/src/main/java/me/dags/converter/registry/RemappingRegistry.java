package me.dags.converter.registry;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.util.log.Logger;

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
    public T getVal(int id) {
        try {
            T source = registry.getVal(id);
            if (registry.isDefault(source)) {
                int block = BlockState.getBlockId(id);
                int meta = BlockState.getMetaData(id);
                throw new RuntimeException("Missing value for id: " + id + "(" + block + ":" + meta + ")");
            }
            T result = mapper.apply(source);
            if (result instanceof Biome) {
                return result;
            }
            if (result == source) {
                throw new RuntimeException("Unable to remap value: " + source);
            }
            return result;
        } catch (RuntimeException e) {
            Logger.log(e).flush();
            System.exit(1);
            return null;
        }
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
