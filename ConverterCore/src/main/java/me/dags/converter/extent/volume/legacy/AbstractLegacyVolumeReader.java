package me.dags.converter.extent.volume.legacy;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.nibble.ChunkNibbleArray;
import me.dags.converter.util.storage.ShortArray;
import me.dags.converter.util.storage.nibble.NibbleArray;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

public abstract class AbstractLegacyVolumeReader implements Volume.Reader {

    protected final ShortArray blocks;
    protected final CompoundTag root;
    protected final NibbleArray metas;
    protected final Registry<BlockState> registry;

    public AbstractLegacyVolumeReader(Registry<BlockState> registry, CompoundTag section, NibbleArray.Factory factory) {
        this.root = section;
        this.registry = registry;
        byte[] blocks = section.getByteArrayTag("Blocks").getValue();
        byte[] adds = section.getByteArrayTag("Add").getValue();
        byte[] data = section.getByteArrayTag("Data").getValue();
        this.blocks = new ShortArray(blocks, adds);
        this.metas = factory.read(data);
        if (registry == null) {
            throw new NullPointerException("palette");
        }
    }

    @Override
    public int size() {
        return blocks.getData().length;
    }

    @Override
    public BlockState getState(int x, int y, int z) {
        int index = indexOf(x, y, z);
        int blockId = getBlockId(index, blocks);
        int metadata = getMetadata(index, metas);
        if (blockId == 68 && metadata < 2) {
            System.out.println(blockId + ":" + metadata);
            System.exit(1);
        }
        int stateId = BlockState.getStateId(blockId, metadata);
        return registry.getVal(stateId);
    }

    @Override
    public Tag<?> getData(String key) {
        return root.get(key);
    }

    protected int getBlockId(int index, ShortArray blocks) {
        return blocks.getShort(index);
    }

    protected int getMetadata(int index, NibbleArray metas) {
        return metas.getNibble(index);
    }
}
