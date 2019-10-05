package me.dags.converter.biome;

import java.util.function.UnaryOperator;

public class BiomeMapper implements UnaryOperator<Biome> {
    @Override
    public Biome apply(Biome biome) {
        return biome;
    }
}
