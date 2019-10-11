package me.dags.converter.extent;

import me.dags.converter.converter.ReaderFactory;
import me.dags.converter.converter.WriterFactory;
import me.dags.converter.version.Version;

public enum Format {
    NONE("none"),
    WORLD("world"),
    SCHEMATIC("schematic"),
    STRUCTURE("nbt"),
    ;

    private final String identifier;

    Format(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ReaderFactory reader(Version version) {
        switch (this) {
            case SCHEMATIC:
                return version::schematicReader;
            case STRUCTURE:
                return version::structureReader;
            case WORLD:
                return version::chunkReader;
        }
        return null;
    }

    public WriterFactory writer(Version version) {
        switch (this) {
            case SCHEMATIC:
                return version::schematicWriter;
            case STRUCTURE:
                return version::structureWriter;
            case WORLD:
                return version::chunkWriter;
        }
        return null;
    }
}
