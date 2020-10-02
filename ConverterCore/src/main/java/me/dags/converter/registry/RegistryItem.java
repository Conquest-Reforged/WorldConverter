package me.dags.converter.registry;

import java.text.ParseException;

public interface RegistryItem<T> {

    String getIdentifier();

    int getId();

    T parseExtended(String properties) throws ParseException;
}
