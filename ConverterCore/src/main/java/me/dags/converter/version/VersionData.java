package me.dags.converter.version;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.dags.converter.biome.Biome;
import me.dags.converter.block.BlockState;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.log.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class VersionData {

    public final Version version;
    public final Registry<Biome> biomes;
    public final Registry<BlockState> blocks;

    public VersionData(Version version, Registry<BlockState> blocks, Registry<Biome> biomes) {
        this.version = version;
        this.blocks = blocks;
        this.biomes = biomes;
    }

    public static VersionData loadGameData(File file, Version version) throws Exception {
        if (file.getPath().endsWith(".json")) {
            Logger.log("Loading custom GameData", file);
            try (Reader reader = new BufferedReader(new FileReader(file))) {
                JsonElement element = new JsonParser().parse(reader);
                if (element.isJsonObject()) {
                    return version.parseGameData(element.getAsJsonObject());
                } else {
                    Logger.log("Invalid GameData format:", element);
                }
            } catch (Exception e) {
                Logger.log(e);
            }
        }
        return version.loadGameData();
    }
}
