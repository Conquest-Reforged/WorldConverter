package me.dags.converter.version.format;

import me.dags.converter.biome.convert.BiomeContainer;
import me.dags.converter.biome.convert.BiomeContainer114;
import me.dags.converter.biome.convert.BiomeContainer115;
import org.jnbt.Tag;

public enum BiomeFormat {
    LEGACY {
        @Override
        public BiomeContainer.Reader newReader(Tag<?> tag) {
            return BiomeContainer114.reader(tag);
        }

        @Override
        public BiomeContainer.Writer newWriter() {
            return BiomeContainer114.writer();
        }
    },
    LATEST {
        @Override
        public BiomeContainer.Reader newReader(Tag<?> tag) {
            return BiomeContainer115.reader(tag);
        }

        @Override
        public BiomeContainer.Writer newWriter() {
            return BiomeContainer115.writer();
        }
    },
    ;

    public abstract BiomeContainer.Reader newReader(Tag<?> tag);

    public abstract BiomeContainer.Writer newWriter();
}
