package me.dags.converter.config;

import me.dags.converter.extent.Format;
import me.dags.converter.version.MinecraftVersion;
import me.dags.converter.version.Version;

import java.io.File;

public class Config {

    public Data input = new Data();
    public Data output = new Data();
    public CustomData custom = new CustomData();

    public Config copy() {
        Config config = new Config();
        config.input.file = input.file;
        config.input.format = input.format;
        config.input.version = input.version;
        config.output.file = output.file;
        config.output.format = output.format;
        config.output.version = output.version;
        config.custom.dataIn.set(custom.dataIn.getPath());
        config.custom.dataOut.set(custom.dataOut.getPath());
        config.custom.blocks.set(custom.blocks.getPath());
        config.custom.biomes.set(custom.biomes.getPath());
        return config;
    }

    public static class Data {

        public File file = new File("");
        public Format format = Format.NONE;
        public Version version = MinecraftVersion.DETECT;
    }
}
