package me.dags.converter.version.format;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.structure.StructureReader;
import me.dags.converter.extent.structure.StructureWriter;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

public enum StructureFormat {
    INSTANCE
    ;

    public Extent.Reader newReader(Registry<BlockState> registry, CompoundTag root) throws Exception {
        return new StructureReader(registry, root);
    }

    public Extent.Writer newWriter(WriterConfig config) {
        return new StructureWriter(config);
    }
}
