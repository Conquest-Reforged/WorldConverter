package me.dags.converter;

import me.dags.converter.converter.config.Config;
import me.dags.converter.extent.ExtentType;
import me.dags.converter.extent.Format;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import me.dags.converter.util.progress.ProgressBar;
import me.dags.converter.version.versions.MinecraftVersion;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class HeadlessConverter {

    public static void run(String[] args) throws Exception {
        Config config = loadConfig(args);
        if (config.input.file.getPath().isEmpty()) {
            Logger.log("Please specify a file/folder in the config (file=)");
            return;
        }

        ExtentType extentType = ExtentType.guessType(config.input.file);
        if (config.input.format == Format.NONE) {
            if (!extentType.isValid()) {
                Logger.log("Unable to determine the input format");
                return;
            }
            config.input.format = extentType.a;
        }

        if (config.input.version == MinecraftVersion.DETECT) {
            if (!extentType.isValid()) {
                Logger.log("Unable to determine the input version");
                return;
            }
            config.input.version = extentType.b;
        }

        if (config.output.format == Format.NONE) {
            config.output.format = config.input.format;
            if (config.input.format == Format.SCHEMATIC && config.input.version == MinecraftVersion.V1_12) {
                config.output.format = Format.STRUCTURE;
            }
        } else if (config.input.format == Format.WORLD && config.output.format != Format.WORLD) {
            Logger.log("Cannot convert the world format to other formats");
            return;
        } else if (config.output.format == Format.WORLD && config.input.format != Format.WORLD) {
            Logger.log("Cannot convert other formats to the world format");
            return;
        }

        if (config.output.version == MinecraftVersion.DETECT) {
            config.output.version = MinecraftVersion.V1_14;
        }

        Main.convert(config, ProgressBar.console());
    }

    private static Config loadConfig(String[] args) throws IOException {
        String path = getPath(args);
        if (path.isEmpty()) {
            path = "config.txt";
        }

        File file = new File(path).getAbsoluteFile();
        if (!file.exists()) {
            IO.copy(IO.open("/config.txt"), file);
        }

        Config config = new Config();
        try (Scanner scanner = new Scanner(IO.read(file))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    continue;
                }
                String[] pair = line.split("=");
                if (pair.length == 1) {
                    String key = pair[0].trim();
                    String val = "";
                    set(config, key.trim(), val.trim());
                }
                if (pair.length == 2) {
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    set(config, key.trim(), val.trim());
                }
            }
        }
        return config;
    }

    private static String getPath(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-nogui")) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(arg);
        }
        return sb.toString();
    }

    private static void set(Config config, String key, String val) {
        switch (key) {
            case "file":
                config.input.file = new File(val);
                return;
            case "input_format":
                config.input.format = Format.valueOf(val.toUpperCase());
                return;
            case "input_version":
                config.input.version = MinecraftVersion.parse(val);
                return;
            case "output_format":
                config.output.format = Format.valueOf(val.toUpperCase());
                return;
            case "output_version":
                config.output.version = MinecraftVersion.parse(val);
                return;
            case "data_in":
                config.custom.dataIn.set(val);
                return;
            case "data_out":
                config.custom.dataOut.set(val);
                return;
            case "block_mappings":
                config.custom.blocks.set(val);
                return;
            case "biome_mappings":
                config.custom.biomes.set(val);
        }
    }
}
