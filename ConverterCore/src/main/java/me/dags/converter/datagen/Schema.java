package me.dags.converter.datagen;

import me.dags.converter.datagen.biome.BiomeData;
import me.dags.converter.datagen.biome.BiomeWriter;
import me.dags.converter.datagen.block.BlockData;
import me.dags.converter.datagen.block.BlockWriterLegacy;
import me.dags.converter.datagen.writer.ValueWriter;

public class Schema {

    public final String version;
    public final ValueWriter<BlockData> block;
    public final ValueWriter<BiomeData> biome;

    public Schema(String version, ValueWriter<BlockData> block, ValueWriter<BiomeData> biome) {
        this.version = version;
        this.block = block;
        this.biome = biome;
    }

    public static Schema legacy(String version) {
        return new Schema(version, new BlockWriterLegacy(), new BiomeWriter());
    }

    public static Schema modern(String version) {
        return new Schema(version, new BlockWriterLegacy(), new BiomeWriter());
    }
}
