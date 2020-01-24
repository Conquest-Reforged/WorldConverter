package me.dags.converter.biome.convert;

import org.jnbt.IntArrayTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;

public class BiomeContainer115 implements BiomeContainer {

    private static final int ZOOM_HORIZ = (int) Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int ZOOM_VERT = (int) Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    public static final int SIZE = 1 << ZOOM_HORIZ + ZOOM_HORIZ + ZOOM_VERT;
    public static final int MASK_HORIZ = (1 << ZOOM_HORIZ) - 1;
    public static final int MASK_VERT = (1 << ZOOM_VERT) - 1;

    protected final int[] biomes;

    public BiomeContainer115(int[] biomes) {
        this.biomes = biomes;
    }

    @Override
    public int sizeX() {
        return 16;
    }

    @Override
    public int sizeY() {
        return 64;
    }

    @Override
    public int sizeZ() {
        return 16;
    }

    public static BiomeContainer.Reader reader(Tag<?> tag) {
        IntArrayTag data = (IntArrayTag) tag;
        return new BiomeContainer115.Reader(data.getValue());
    }

    public static BiomeContainer.Writer writer() {
        return new Writer();
    }

    private static int indexOf(int x, int y, int z) {
        int bx = x & MASK_HORIZ;
        int by = clamp(y, 0, MASK_VERT);
        int bz = z & MASK_HORIZ;
        return by << ZOOM_HORIZ + ZOOM_HORIZ | bz << ZOOM_HORIZ | bx;
    }

    private static class Reader extends BiomeContainer115 implements BiomeContainer.Reader {

        public Reader(int[] biomes) {
            super(biomes);
        }

        @Override
        public int get(int x, int y, int z) {
            int index = indexOf(x, y, z);
            if (index < biomes.length) {
                return biomes[index];
            }
            return 0;
        }
    }

    private static class Writer extends BiomeContainer115 implements BiomeContainer.Writer {

        public Writer() {
            super(new int[4 * 4 * 64]);
        }

        @Override
        public Tag<?> getTag() {
            return Nbt.tag(biomes);
        }

        @Override
        public void set(int x, int y, int z, int biome) {
            int index = indexOf(x, y, z);
            if (index < biomes.length) {
                biomes[index] = biome;
            }
        }
    }

    private static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }
}
