package me.dags.converter.biome.convert;

import org.jnbt.Tag;

public interface BiomeContainer {

    int sizeX();

    int sizeY();

    int sizeZ();

    interface Reader extends BiomeContainer {

        int get(int x, int y, int z);
    }

    interface Writer extends BiomeContainer {

        Tag<?> getTag();

        void set(int x, int y, int z, int biome);
    }
}
