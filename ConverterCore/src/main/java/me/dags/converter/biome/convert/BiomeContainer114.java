package me.dags.converter.biome.convert;

import org.jnbt.ByteArrayTag;
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

    public static BiomeContainer.Reader reader(Tag<?> data) {
        ByteArrayTag biomes = (ByteArrayTag) data;
        return new BiomeContainer114.Reader(biomes.getValue());
    }

    public static BiomeContainer.Writer writer() {
        return new Writer();
    }

    private static int indexOf(int x, int z) {
        return (z << 4) + x;
    }

    private static class Reader extends BiomeContainer114 implements BiomeContainer.Reader {

        public Reader(byte[] biomes) {
            super(biomes);
        }

        @Override
        public int get(int x, int y, int z) {
            int index = indexOf(x, z);
            if (index < biomes.length) {
                return biomes[index];
            }
            return 0;
        }
    }

    private static class Writer extends BiomeContainer114 implements BiomeContainer.Writer {

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
