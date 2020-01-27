package me.dags.converter.biome.interp;

import me.dags.converter.biome.convert.BiomeContainer;

public interface BiomeInterpolator {

    BiomeInterpolator INTERP_115 = new Interp115();
    BiomeInterpolator INTERP_LEGACY = new InterpLegacy();

    int getBiome(long seed, int x, int y, int z, BiomeContainer.Reader reader);
}
