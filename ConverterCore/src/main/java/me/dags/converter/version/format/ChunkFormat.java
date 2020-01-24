package me.dags.converter.version.format;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.chunk.latest.ChunkReader;
import me.dags.converter.extent.chunk.latest.ChunkWriter;
import me.dags.converter.extent.chunk.legacy.LegacyChunkReader;
import me.dags.converter.extent.chunk.legacy.LegacyChunkWriter;
import me.dags.converter.registry.Registry;
import me.dags.converter.version.Version;
import org.jnbt.CompoundTag;

public enum ChunkFormat {
    LEGACY {
        @Override
        public Chunk.Reader newReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
            return new LegacyChunkReader(registry, root);
        }

        @Override
        public Chunk.Writer newWriter(Version version, WriterConfig config) {
            return new LegacyChunkWriter(version, config);
        }
    },
    LATEST {
        @Override
        public Chunk.Reader newReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
            return new ChunkReader(registry, root);
        }

        @Override
        public Chunk.Writer newWriter(Version version, WriterConfig config) {
            return new ChunkWriter(version);
        }
    },
    ;

    public abstract Chunk.Reader newReader(Registry<BlockState> registry, CompoundTag root) throws Exception;

    public abstract Chunk.Writer newWriter(Version version, WriterConfig config);
}
