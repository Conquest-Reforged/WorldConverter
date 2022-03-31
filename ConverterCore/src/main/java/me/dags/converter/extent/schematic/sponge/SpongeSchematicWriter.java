package me.dags.converter.extent.schematic.sponge;

import me.dags.converter.block.BlockState;
import me.dags.converter.block.registry.PaletteWriter;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.util.storage.VarIntArray;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.LinkedList;
import java.util.List;

public class SpongeSchematicWriter implements Volume.Writer {

    private final int width;
    private final int height;
    private final int length;
    private final VarIntArray blocks;
    protected final CompoundTag root = Nbt.compound();
    private final PaletteWriter<BlockState> palette = new PaletteWriter<>();

    public SpongeSchematicWriter(WriterConfig config) {
        this.width = config.get("Width");
        this.height = config.get("Height");
        this.length = config.get("Length");
        this.blocks = new VarIntArray(width, height, length);
    }

    @Override
    public void setState(int x, int y, int z, BlockState state) {
        int id = palette.getOrCreateId(state);
        int index = indexOf(x, y, z);
        blocks.set(index, id);
    }

    @Override
    public void setData(String key, Tag<?> data) {
        root.put(key, data);
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
    public CompoundTag flush() {
        root.put("Width", getWidth());
        root.put("Height", getHeight());
        root.put("Length", getLength());
        root.put("Palette", flushPalette());
        root.put("PaletteMax", palette.size());
        root.put("Version", 2);
        root.put("BlockData", flushBlocks());
        root.put("Offset", Nbt.tag(new int[]{0, 0, 0}));
        return root;
    }

    private ListTag<CompoundTag> flushPalette() {
        List<CompoundTag> list = new LinkedList<>();
        for (BlockState state : palette) {
            list.add(state.getData());
        }
        return Nbt.list(TagType.COMPOUND, list);
    }

    private ByteArrayTag flushBlocks() {
        return Nbt.tag(blocks.writeBytes());
    }

    public boolean isEmpty() { return false; }
}
