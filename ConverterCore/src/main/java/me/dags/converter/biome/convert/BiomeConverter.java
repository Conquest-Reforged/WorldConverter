package me.dags.converter.biome.convert;

import me.dags.converter.biome.Biome;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.registry.RemappingRegistry;
import me.dags.converter.version.Version;
import org.jnbt.Tag;

public class BiomeConverter implements DataConverter {

    private final long seed;
    private final Version versionIn;
    private final Version versionOut;
    private final RemappingRegistry<Biome> registry;

    public BiomeConverter(long seed, Version versionIn, Version versionOut, RemappingRegistry<Biome> registry) {
        this.seed = seed;
        this.registry = registry;
        this.versionIn = versionIn;
        this.versionOut = versionOut;
    }

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public String getInputKey() {
        return "Biomes";
    }

    @Override
    public String getOutputKey() {
        return "Biomes";
    }

    @Override
    public Tag<?> convert(Tag<?> tag) {
        BiomeContainer.Reader reader = versionIn.getBiomeFormat().newReader(seed, tag);
        BiomeContainer.Writer writer = versionOut.getBiomeFormat().newWriter();
        for (int y = 0; y < writer.sizeY(); y++) {
            for (int z = 0; z < writer.sizeZ(); z++) {
                for (int x = 0; x < writer.sizeX(); x++) {
                    int id = reader.get(x, y, z);
                    Biome in = registry.getInput(id);
                    Biome out = registry.getOutput(in);
                    writer.set(x, y, z, out.getId());
                }
            }
        }
        return writer.getTag();
    }
}
