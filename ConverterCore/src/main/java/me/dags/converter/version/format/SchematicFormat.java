package me.dags.converter.version.format;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.schematic.legacy.LegacySchematicReader;
import me.dags.converter.extent.schematic.legacy.LegacySchematicWriter;
import me.dags.converter.extent.schematic.sponge.SpongeSchematicReader;
import me.dags.converter.extent.schematic.sponge.SpongeSchematicWriter;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

public enum SchematicFormat {
    LEGACY {
        @Override
        public Volume.Reader newReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
            return new LegacySchematicReader(registry, root);
        }

        @Override
        public Volume.Writer newWriter(WriterConfig config) {
            return new LegacySchematicWriter(config);
        }
    },
    SPONGE {
        @Override
        public Volume.Reader newReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
            return new SpongeSchematicReader(registry, root);
        }

        @Override
        public Volume.Writer newWriter(WriterConfig config) {
            return new SpongeSchematicWriter(config);
        }
    }
    ;

    public abstract Volume.Reader newReader(Registry<BlockState> registry, CompoundTag root) throws Exception;

    public abstract Volume.Writer newWriter(WriterConfig config);
}
