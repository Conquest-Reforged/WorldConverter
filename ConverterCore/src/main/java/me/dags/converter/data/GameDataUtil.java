package me.dags.converter.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.dags.converter.converter.config.CustomData;
import me.dags.converter.registry.Mapper;
import me.dags.converter.resource.Container;
import me.dags.converter.resource.Resource;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.Version;

import java.io.*;
import java.nio.file.Paths;

public class GameDataUtil {

    public static GameData loadGameData(File file, Version version) throws Exception {
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

    public static GameData applyMappings(CustomData config, Mappings mappings) throws Exception {
        applyMappings(config.blocks.get(), mappings.getBlocks());
        applyMappings(config.biomes.get(), mappings.getBiomes());
        return mappings.build();
    }

    private static void applyMappings(File file, Mapper.Builder<?> builder) throws Exception {
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
