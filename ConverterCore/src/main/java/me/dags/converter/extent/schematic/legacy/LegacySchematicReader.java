package me.dags.converter.extent.schematic.legacy;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.legacy.AbstractLegacyVolumeReader;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.nibble.ChunkNibbleArray;
import me.dags.converter.util.storage.nibble.NibbleArray;
import me.dags.converter.util.storage.nibble.VolumeNibbleArray;
import org.jnbt.CompoundTag;

public class LegacySchematicReader extends AbstractLegacyVolumeReader {

    private final int width;
    private final int height;
    private final int length;

    public LegacySchematicReader(Registry<BlockState> registry, CompoundTag root) {
        super(registry, root, VolumeNibbleArray.FACTORY);
        this.width = root.getInt("Width");
        this.length = root.getInt("Length");
        this.height = root.getInt("Height");
    }

    @Override
    protected int getMetadata(int index, NibbleArray metas) {
        return metas.getByte(index);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }
}
