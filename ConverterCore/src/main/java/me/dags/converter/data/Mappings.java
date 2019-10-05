package me.dags.converter.data;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.registry.Mapper;
import me.dags.converter.registry.Registry;
import me.dags.converter.registry.RemappingRegistry;
import me.dags.converter.resource.Container;
import me.dags.converter.resource.Resource;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.Version;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

public class Mappings {

    private final GameData from;
    private final GameData to;
    private final Mapper.Builder<BlockState> blocks;
    private final Mapper.Builder<Biome> biomes;

    private Mappings(GameData from, GameData to) {
        this.from = from;
        this.to = to;
        this.blocks = Mapper.builder(from.blocks, to.blocks);
        this.biomes = Mapper.builder(from.biomes, to.biomes);
    }

    public Mapper.Builder<BlockState> getBlocks() {
        return blocks;
    }

    public Mapper.Builder<Biome> getBiomes() {
        return biomes;
    }

    public Mappings builtIn() throws ParseException, IOException {
        String version = from.blocks.getVersion() + "-" + to.blocks.getVersion();
        Path path = Paths.get("mappings", version);
        try (Container container = Container.self(Version.class)) {
            for (Resource resource : container.getResources(path.resolve("blocks"))) {
                Logger.log("Parsing block mappings:", resource.getPath());
                getBlocks().parse(resource.getInputStream());
            }
            for (Resource resource : container.getResources(path.resolve("biomes"))) {
                Logger.log("Parsing biome mappings:", resource.getPath());
                getBiomes().parse(resource.getInputStream());
            }
        }
        return this;
    }

    public GameData build() {
        Registry<BlockState> blocks;
        Registry<Biome> biomes;

        if (from == to) {
            if (getBlocks().hasMappings()) {
                blocks = new RemappingRegistry<>(from.blocks, getBlocks().build());
            } else {
                blocks = from.blocks;
            }

            if (getBiomes().hasMappings()) {
                biomes = new RemappingRegistry<>(from.biomes, getBiomes().build());
            } else {
                biomes = from.biomes;
            }
        } else {
            blocks = new RemappingRegistry<>(from.blocks, getBlocks().build());
            biomes = new RemappingRegistry<>(from.biomes, getBiomes().build());
        }
        return new GameData(from.version, blocks, biomes);
    }

    public static Mappings build(Version from, Version to) throws Exception {
        if (from == to) {
            GameData data = from.loadGameData();
            return build(data, data);
        }
        return build(from.loadGameData(), to.loadGameData());
    }

    public static Mappings build(GameData from, GameData to) {
        return new Mappings(from, to);
    }
}
