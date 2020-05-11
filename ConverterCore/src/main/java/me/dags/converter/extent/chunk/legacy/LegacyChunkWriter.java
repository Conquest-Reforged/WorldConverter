package me.dags.converter.extent.chunk.legacy;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.AbstractChunkWriter;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import me.dags.converter.version.Version;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

public class LegacyChunkWriter extends AbstractChunkWriter {

    private final Registry<BlockState> registry;

    public LegacyChunkWriter(Version version, WriterConfig config) {
        super(version);
        this.registry = config.get("registry");
    }

    @Override
    protected CompoundTag createRoot() {
        return Nbt.compound();
    }

    @Override
    protected Volume.Writer createSection(int index) {
        return new LegacySectionWriter(registry, index);
    }
}
