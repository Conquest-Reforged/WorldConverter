package me.dags.converter.datagen.biome;

public class BiomeData {

    private final String name;
    private final int id;

    public BiomeData(Object name, int id) {
        this.name = "" + name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
