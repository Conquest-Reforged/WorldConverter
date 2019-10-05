package me.dags.converter.extent.schematic.latest;

import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.volume.latest.AbstractVolumeWriter;
import org.jnbt.CompoundTag;

public class SchematicWriter extends AbstractVolumeWriter {

    public SchematicWriter(WriterConfig config) {
        super(config.get("Width"), config.get("Height"), config.get("Length"));
    }

    @Override
    public CompoundTag flush() {
        return super.flush()
                .put("Width", getWidth())
                .put("Height", getHeight())
                .put("Length", getLength());
    }
}
