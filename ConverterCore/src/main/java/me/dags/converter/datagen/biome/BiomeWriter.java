package me.dags.converter.datagen.biome;

import me.dags.converter.datagen.writer.DataWriter;
import me.dags.converter.datagen.writer.ValueWriter;

import java.io.IOException;

public class BiomeWriter implements ValueWriter<BiomeData> {
    @Override
    public void write(BiomeData biome, DataWriter writer) throws IOException {
        writer.name(biome.getName()).value(biome.getId());
    }
}
