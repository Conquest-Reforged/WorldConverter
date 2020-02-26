package me.dags.converter.converter;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.converter.config.CustomData;
import me.dags.converter.datagen.Mappings;
import me.dags.converter.registry.Mapper;
import me.dags.converter.registry.RemappingRegistry;
import me.dags.converter.resource.Container;
import me.dags.converter.resource.Resource;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.Version;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

public class ConverterData {

    public final Version version;
    public final RemappingRegistry<Biome> biomes;
    public final RemappingRegistry<BlockState> blocks;

    public ConverterData(Version version, RemappingRegistry<BlockState> blocks, RemappingRegistry<Biome> biomes) {
        this.version = version;
        this.biomes = biomes;
        this.blocks = blocks;
    }

    public static ConverterData create(CustomData config, Mappings mappings) throws Exception {
        apply(config.blocks.get(), mappings.getBlocks());
        apply(config.biomes.get(), mappings.getBiomes());
        return mappings.build();
    }

    private static void apply(File file, Mapper.Builder<?> builder) throws Exception {
        if (!file.getPath().isEmpty() || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            try (Container container = Container.open(file.getAbsolutePath())) {
                for (Resource resource : container.getResources(Paths.get(""))) {
                    Logger.log("Loading custom mappings:", resource.getPath());
                    if (resource.getPath().endsWith(".txt")) {
                        try (InputStream in = new BufferedInputStream(resource.getInputStream())) {
                            builder.parse(in);
                        }
                    }
                }
            }
        } else if (file.getName().endsWith(".txt")) {
            Logger.log("Loading custom mappings:", file.getPath());
            try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                builder.parse(in);
            }
        }
    }
}
