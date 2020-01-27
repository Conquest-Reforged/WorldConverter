package me.dags.converter.version.format;

import me.dags.converter.biome.convert.BiomeContainer;
import me.dags.converter.biome.convert.BiomeContainer114;
import me.dags.converter.biome.convert.BiomeContainer115;
import me.dags.converter.biome.interp.BiomeInterpolator;
import me.dags.converter.biome.interp.InterpolatedReader;
import org.jnbt.Tag;

public enum BiomeFormat {
    LEGACY {
        @Override
        public BiomeContainer.Reader newReader(long seed, Tag<?> tag) {
            return new BiomeContainer114.Reader(tag.asByteArray().getValue());
        }

        @Override
        public BiomeContainer.Writer newWriter() {
            return new BiomeContainer114.Writer();
        }
    },
    LATEST {
        @Override
        public BiomeContainer.Reader newReader(long seed, Tag<?> tag) {
            return new InterpolatedReader(
                    seed,
                    new BiomeContainer115.Reader(tag.asIntArray().getValue()),
                    BiomeInterpolator.INTERP_115
            );
        }

        @Override
        public BiomeContainer.Writer newWriter() {
            return new BiomeContainer115.Writer();
        }
    },
    ;

    public BiomeContainer.Reader newReader(Tag<?> tag) {
        return newReader(0L, tag);
    }

    public abstract BiomeContainer.Reader newReader(long seed, Tag<?> tag);

    public abstract BiomeContainer.Writer newWriter();
}
