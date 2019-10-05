package me.dags.converter.extent.schematic.legacy;

import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.volume.legacy.AbstractLegacyVolumeWriter;
import me.dags.converter.util.storage.nibble.VolumeNibbleArray;
import org.jnbt.CompoundTag;

public class LegacySchematicWriter extends AbstractLegacyVolumeWriter {

    public LegacySchematicWriter(WriterConfig config) {
        super(config.get("data"), config.get("Width"), config.get("Height"), config.get("Length"), VolumeNibbleArray.FACTORY);
    }

    @Override
    public CompoundTag flush() {
        return super.flush()
                .put("Width", getWidth())
                .put("Height", getHeight())
                .put("Length", getLength())
                .put("Materials", "Alpha");
    }
}
