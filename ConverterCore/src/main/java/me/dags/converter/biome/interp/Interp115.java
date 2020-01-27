package me.dags.converter.biome.interp;

import me.dags.converter.biome.convert.BiomeContainer;

public class Interp115 implements BiomeInterpolator {

    @Override
    public int getBiome(long seed, int x, int y, int z, BiomeContainer.Reader reader) {
        int minX = x - 2;
        int minY = y - 2;
        int minZ = z - 2;
        int quadMinX = minX >> 2;
        int quadMinY = minY >> 2;
        int quadMinZ = minZ >> 2;
        double quadX = (double) (minX & 3) / 4.0D;
        double quadY = (double) (minY & 3) / 4.0D;
        double quadZ = (double) (minZ & 3) / 4.0D;
        double[] noise = new double[8];

        for (int i = 0; i < 8; ++i) {
            boolean flag = (i & 4) == 0;
            boolean flag1 = (i & 2) == 0;
            boolean flag2 = (i & 1) == 0;
            int noiseX = flag ? quadMinX : quadMinX + 1;
            int noiseY = flag1 ? quadMinY : quadMinY + 1;
            int noiseZ = flag2 ? quadMinZ : quadMinZ + 1;
            double dx = flag ? quadX : quadX - 1.0D;
            double dy = flag1 ? quadY : quadY - 1.0D;
            double dz = flag2 ? quadZ : quadZ - 1.0D;
            noise[i] = getNoiseValue(seed, noiseX, noiseY, noiseZ, dx, dy, dz);
        }

        int index = 0;
        double maxNoise = noise[0];

        for (int i = 1; i < 8; ++i) {
            if (maxNoise > noise[i]) {
                index = i;
                maxNoise = noise[i];
            }
        }

        int biomeX = (index & 4) == 0 ? quadMinX : quadMinX + 1;
        int biomeY = (index & 2) == 0 ? quadMinY : quadMinY + 1;
        int biomeZ = (index & 1) == 0 ? quadMinZ : quadMinZ + 1;

        return reader.get(biomeX, biomeY, biomeZ);
    }

    private static double getNoiseValue(long seed, int x, int y, int z, double dx, double dy, double dz) {
        long hash = hash(seed, x);
        hash = hash(hash, y);
        hash = hash(hash, z);
        hash = hash(hash, x);
        hash = hash(hash, y);
        hash = hash(hash, z);
        double d0 = lerp(hash);
        hash = hash(hash, seed);
        double d1 = lerp(hash);
        hash = hash(hash, seed);
        double d2 = lerp(hash);
        return square(dz + d2) + square(dy + d1) + square(dx + d0);
    }

    public static long hash(long p_226162_0_, long p_226162_2_) {
        p_226162_0_ = p_226162_0_ * (p_226162_0_ * 6364136223846793005L + 1442695040888963407L);
        p_226162_0_ = p_226162_0_ + p_226162_2_;
        return p_226162_0_;
    }

    private static double lerp(long hash) {
        double d0 = (double) ((int) Math.floorMod(hash >> 24, 1024L)) / 1024.0D;
        return (d0 - 0.5D) * 0.9D;
    }

    private static double square(double value) {
        return value * value;
    }
}
