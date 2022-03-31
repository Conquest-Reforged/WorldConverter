package me.dags.converter.extent.volume.latest;

import me.dags.converter.block.BlockState;
import me.dags.converter.block.registry.PaletteWriter;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.util.storage.BitArray;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.LongArrayTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractVolumeWriter implements Volume.Writer {

    private final int width;
    private final int height;
    private final int length;
    private final int size;
    protected final CompoundTag root = Nbt.compound();
    private final PaletteWriter<BlockState> palette = new PaletteWriter<>();

    private int[] buffer = null;

    public AbstractVolumeWriter(int width, int height, int length) {
        this.size = width * height * length;
        this.width = width;
        this.height= height;
        this.length = length;
        this.setState(0, 0, 0, BlockState.AIR);
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
        if (buffer == null) {
            buffer = new int[size];
        }
        int index = indexOf(x, y, z);
        int stateId = palette.getOrCreateId(state);
        buffer[index] = stateId;
    }

    @Override
    public void setData(String key, Tag<?> data) {
        root.put(key, data);
    }

    @Override
    public CompoundTag flush() {
        if (buffer != null) {
            root.put("Palette", flushPalette());
            root.put("BlockStates", flushBlocks());
        }
        return root;
    }

    private ListTag<CompoundTag> flushPalette() {
        List<CompoundTag> list = new LinkedList<>();
        for (BlockState state : palette) {
            list.add(state.getData());
        }
        return Nbt.list(TagType.COMPOUND, list);
    }

    private LongArrayTag flushBlocks() {
        int maxId = palette.size() - 1;
        int bits = BitArray.minBits(maxId);
        BitArray array = new BitArray(bits, buffer.length);
        for (int i = 0; i < buffer.length; i++) {
            array.setBits(i, buffer[i]);
        }
        return Nbt.tag(array.getArray());
    }
}
