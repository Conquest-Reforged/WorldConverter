package me.dags.converter.biome.convert;

import org.jnbt.Nbt;
import org.jnbt.Tag;

public class BiomeContainer114 implements BiomeContainer {

    protected final byte[] biomes;

    public BiomeContainer114(byte[] biomes) {
        this.biomes = biomes;
    }

    @Override
    public int sizeX() {
        return 16;
    }

    @Override
    public int sizeY() {
        return 1;
    }

    @Override
    public int sizeZ() {
        return 16;
    }

    private static int indexOf(int x, int z) {
        return (z << 4) + x;
    }

    public static class Reader extends BiomeContainer114 implements BiomeContainer.Reader {

        public Reader(byte[] biomes) {
            super(biomes);
        }

        @Override
        public int get(int x, int y, int z) {
            int index = indexOf(x, z);
            if (index < biomes.length) {
                return (255 & (int) biomes[index]);
            }
            return 0;
        }
    }

    public static class Writer extends BiomeContainer114 implements BiomeContainer.Writer {

        public Writer() {
            super(new byte[16 * 16]);
        }

        @Override
        public Tag<?> getTag() {
            return Nbt.tag(biomes);
        }

        @Override
        public void set(int x, int y, int z, int biome) {
            int index = indexOf(x, z);
            if (index < biomes.length) {
                biomes[index] = (byte) biome;
            }
        }
    }
}
