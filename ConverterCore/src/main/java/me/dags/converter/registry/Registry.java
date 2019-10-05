package me.dags.converter.registry;

import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.text.ParseException;
import java.util.List;

public interface Registry<T extends RegistryItem> extends Iterable<T> {

    int size();

    String getVersion();

    T getVal(int id);

    default boolean isDefault(T t) {
        return false;
    }

    int getId(T val);

    Parser<T> getParser();

    interface Writer<T extends RegistryItem> extends Iterable<T> {

        int getOrCreateId(T val);

        int size();
    }

    interface Reader<T extends RegistryItem> {

        T getVal(int id);
    }

    interface Mapper<T extends RegistryItem> {

        T apply(T in);

        String getVersion();
    }

    interface Parser<T extends RegistryItem> {

        T parse(String in) throws ParseException;

        T parse(CompoundTag in) throws ParseException;

        Reader<T> parsePalette(List<Tag<CompoundTag>> list) throws ParseException;
    }
}
