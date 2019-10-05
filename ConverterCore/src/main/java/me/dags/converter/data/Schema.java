package me.dags.converter.data;

import me.dags.converter.data.biome.BiomeData;
import me.dags.converter.data.biome.BiomeWriter;
import me.dags.converter.data.block.BlockData;
import me.dags.converter.data.block.BlockWriter;
import me.dags.converter.data.block.BlockWriterLegacy;
import me.dags.converter.data.writer.ValueWriter;

public class Schema {

    public final String version;
    public final ValueWriter<BlockData> block;
    public final ValueWriter<BiomeData> biome;

    public Schema(String version, ValueWriter<BlockData> block, ValueWriter<BiomeData> biome) {
        this.version = version;
        this.block = block;
        this.biome = biome;
    }

    public static Schema forVersion(String version) {
        if (version.startsWith("1.12")) {
            return new Schema(version, new BlockWriterLegacy(), new BiomeWriter());
        } else {
            return new Schema(version, new BlockWriter(), new BiomeWriter());
        }
    }
}
