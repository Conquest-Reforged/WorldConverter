package me.dags.converter.biome.interp;

import me.dags.converter.biome.convert.BiomeContainer;

public class InterpLegacy implements BiomeInterpolator {
    @Override
    public int getBiome(long seed, int x, int y, int z, BiomeContainer.Reader reader) {
        return reader.get(x, y, z);
    }
}
