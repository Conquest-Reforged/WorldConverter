package me.dags.converter.data;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.registry.Registry;
import me.dags.converter.version.Version;

public class GameData {

    public final Version version;
    public final Registry<Biome> biomes;
    public final Registry<BlockState> blocks;

    public GameData(Version version, Registry<BlockState> blocks, Registry<Biome> biomes) {
        this.version = version;
        this.blocks = blocks;
        this.biomes = biomes;
    }
}
