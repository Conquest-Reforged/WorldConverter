package me.dags.converter.extent;

import me.dags.converter.util.IO;
import me.dags.converter.util.Pair;
import me.dags.converter.version.Version;
import me.dags.converter.version.versions.MinecraftVersion;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class ExtentType extends Pair<Format, Version> {

    private ExtentType(Format format, Version version) {
        super(format, version);
    }

    public boolean isValid() {
        return a != null && b != null;
    }

    public static ExtentType guessType(File file)  {
        return guessType(file, 0);
    }

    private static ExtentType guessType(File file, int depth)  {
        if (depth++ > 7) {
            return new ExtentType(null, null);
        }
        if (file.isDirectory()) {
            File level = new File(file, "level.dat");
            if (level.exists()) {
                return guessType(level, depth);
            }
            for (File child : IO.list(file)) {
                ExtentType type = guessType(child, depth);
                if (type.isValid()) {
                    return type;
                }
            }
        } else {
            try {
                CompoundTag root = getCompound(file);
                if (root.get("Blocks").isPresent() && root.get("Data").isPresent()) {
                    return new ExtentType(Format.SCHEMATIC, MinecraftVersion.V1_12);
                }
                if (root.get("BlockData").isPresent() && root.get("Palette").isPresent()) {
                    return new ExtentType(Format.SCHEMATIC, MinecraftVersion.V1_14);
                }
                if (root.get("blocks").isPresent() && root.get("palette").isPresent()) {
                    return new ExtentType(Format.STRUCTURE, MinecraftVersion.V1_14);
                }
                if (root.get("Data").isPresent()) {
                    CompoundTag version = root.get("Data", "Version").asCompound();
                    if (version.isPresent()) {
                        String name = version.getString("Name");
                        for (Version v : MinecraftVersion.values()) {
                            if (name.startsWith(v.getVersion())) {
                                return new ExtentType(Format.WORLD, v);
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {

            }
        }
        return new ExtentType(null, null);
    }

    private static CompoundTag getCompound(File file) throws IOException {
        try (InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            return Nbt.read(in).getTag().asCompound();
        }
    }
}
