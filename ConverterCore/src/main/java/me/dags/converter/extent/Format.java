package me.dags.converter.extent;

import me.dags.converter.converter.ReaderFactory;
import me.dags.converter.converter.WriterFactory;
import me.dags.converter.version.Version;

public enum Format {
    NONE("none"),
    WORLD("world"),
    SCHEMATIC("schematic", "schematic", "schem"),
    STRUCTURE("nbt", "nbt"),
    ;

    private final String identifier;
    private final String[] suffixes;

    Format(String identifier, String... suffixes) {
        this.identifier = identifier;
        this.suffixes = suffixes;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSuffix(String name) {
        for (String s : suffixes) {
            if (name.endsWith(s)) {
                return s;
            }
        }
        return "";
    }

    public boolean hasSuffix(String in) {
        for (String s : suffixes) {
            if (in.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public ReaderFactory reader(Version version) {
        switch (this) {
            case SCHEMATIC:
                return version.getSchematicFormat()::newReader;
            case STRUCTURE:
                return version.getStructureFormat()::newReader;
            case WORLD:
                return version.getChunkFormat()::newReader;
        }
        return null;
    }

    public WriterFactory writer(Version version) {
        switch (this) {
            case SCHEMATIC:
                return version.getSchematicFormat()::newWriter;
            case STRUCTURE:
                return version.getStructureFormat()::newWriter;
            case WORLD:
                return cfg -> version.getChunkFormat().newWriter(version, cfg);
        }
        return null;
    }
}
