package me.dags.converter.registry;

import me.dags.converter.util.storage.IntMap;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public abstract class AbstractRegistry<T extends RegistryItem> implements Registry<T> {

    private final int maxId;
    private final T fallback;
    private final String version;
    protected final IntMap<T> idToVal;
    protected final Map<T, Integer> valToId;

    protected AbstractRegistry(Builder<T> builder) {
        this.idToVal = builder.idToVal;
        this.valToId = builder.valToId;
        this.version = builder.version;
        this.fallback = builder.fallback;
        this.maxId = builder.maxId;
    }

    @Override
    public int size() {
        return valToId.size();
    }

    @Override
    public int maxId() {
        return maxId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isDefault(T t) {
        return t == fallback;
    }

    @Override
    public T getValue(int id) {
        return idToVal.getOrDefault(id, fallback);
    }

    @Override
    public int getId(T val) {
        return valToId.getOrDefault(val, fallback.getId());
    }

    @Override
    public Iterator<T> iterator() {
        return idToVal.iterator();
    }

    public T parse(T in) throws ParseException {
        Integer id = valToId.get(in);
        if (id == null) {
            throw new ParseException("[Registry:" + getVersion() + "] Unknown item: " + in, -1);
        }
        return getValue(id);
    }

    public static class Builder<T extends RegistryItem> {

        private int maxId = -1;
        private final IntMap<T> idToVal;
        private final Map<T, Integer> valToId;
        private final Function<Builder<T>, Registry<T>> constructor;
        private final AtomicInteger id = new AtomicInteger(0);

        private final String version;
        private final T fallback;

        public Builder(String version, T def, int size, Function<Builder<T>, Registry<T>> constructor) {
            this.version = version;
            this.fallback = def;
            this.idToVal = new IntMap<>(size);
            this.valToId = new HashMap<>(size);
            this.constructor = constructor;
        }

        public Builder<T> add(T val) throws IdCollisionException {
            return add(id.getAndAdd(1), val);
        }

        public Builder<T> add(int id, T val) throws IdCollisionException {
            T current = idToVal.get(id);
            if (current != null && !current.equals(val)) {
                throw new IdCollisionException(String.format("Current: %s, New: %s, id: %s", current, val, id));
            }
            valToId.put(val, id);
            idToVal.put(id, val);
            return recordId(id);
        }

        public Builder<T> addUnchecked(T val) {
            return addUnchecked(id.getAndAdd(1), val);
        }

        public Builder<T> addUnchecked(int id, T val) {
            valToId.put(val, id);
            idToVal.put(id, val);
            return recordId(id);
        }

        private Builder<T> recordId(int id) {
            maxId = Math.max(id, maxId);
            return this;
        }

        public Registry<T> build() {
            return constructor.apply(this);
        }
    }
}
