package me.dags.converter.biome.interp;

import me.dags.converter.biome.convert.BiomeContainer;

public class InterpolatedReader implements BiomeContainer.Reader {

    private final long seed;
    private final BiomeContainer.Reader reader;
    private final BiomeInterpolator interpolator;

    public InterpolatedReader(long seed, Reader reader, BiomeInterpolator interpolator) {
        this.seed = seed;
        this.reader = reader;
        this.interpolator = interpolator;
    }

    @Override
    public int sizeX() {
        return reader.sizeX();
    }

    @Override
    public int sizeY() {
        return reader.sizeY();
    }

    @Override
    public int sizeZ() {
        return reader.sizeZ();
    }

    @Override
    public int get(int x, int y, int z) {
        return interpolator.getBiome(seed, x, y, z, reader);
    }
}
