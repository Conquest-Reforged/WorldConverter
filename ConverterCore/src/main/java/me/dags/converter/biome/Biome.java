package me.dags.converter.biome;

import me.dags.converter.registry.RegistryItem;

public class Biome implements RegistryItem<Biome> {

    public static final int MAX_ID = 999;
    public static final Biome NONE = new Biome("ocean", 0);

    private final String name;
    private final int id;

    public Biome(Object name, int id) {
        this.name = "" + name;
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Biome parseExtended(String properties) {
        return null;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == Biome.class && name.equals(((Biome) obj).name);
    }
}
