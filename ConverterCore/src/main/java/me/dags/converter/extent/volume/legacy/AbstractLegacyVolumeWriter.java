package me.dags.converter.extent.volume.legacy;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.ShortArray;
import me.dags.converter.util.storage.nibble.NibbleArray;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;

public abstract class AbstractLegacyVolumeWriter implements Volume.Writer {

    private final Registry<BlockState> registry;
    private final int width;
    private final int height;
    private final int length;
    protected final ShortArray blocks;
    protected final NibbleArray metas;
    protected final CompoundTag root = Nbt.compound();

    protected AbstractLegacyVolumeWriter(Registry<BlockState> registry, int width, int height, int length, NibbleArray.Factory factory) {
        this(registry, width, height, length, width * height * length, factory);
    }

    protected AbstractLegacyVolumeWriter(Registry<BlockState> registry, int width, int height, int length, int metaSize, NibbleArray.Factory factory) {
        this.registry = registry;
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = new ShortArray(width * height * length);
        this.metas = factory.write(metaSize);
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

    @Override
    public void setState(int x, int y, int z, BlockState state) {
        int index = indexOf(x, y, z);
        int stateId = registry.getId(state);
        int blockId = BlockState.getBlockId(stateId);
        int metadata = BlockState.getMetaData(stateId);
        setBlock(index, blockId, blocks);
        setMetadata(index, (byte) metadata, metas);
    }

    @Override
    public void setData(String key, Tag<?> data) {
        root.put(key, data);
    }

    @Override
    public CompoundTag flush() {
        root.put("Blocks", blocks.getData());
        root.put("Data", metas.getData());
        if (blocks.getAdds().length > 0) {
            root.put("Adds", blocks.getAdds());
        }
        return root;
    }

    protected void setBlock(int index, int value, ShortArray blocks) {
        blocks.setShort(index, value);
    }

    protected void setMetadata(int index, byte value, NibbleArray metas) {
        metas.setNibble(index, value);
    }
}
