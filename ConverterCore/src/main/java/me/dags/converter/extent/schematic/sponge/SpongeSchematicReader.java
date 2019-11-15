package me.dags.converter.extent.schematic.sponge;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.VarIntArray;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.text.ParseException;

public class SpongeSchematicReader implements Volume.Reader {

    private final int width;
    private final int height;
    private final int length;
    private final CompoundTag root;
    private final VarIntArray blocks;
    private final Registry.Reader<BlockState> palette;

    public SpongeSchematicReader(Registry<BlockState> registry, CompoundTag root) throws ParseException {
        int maxId = root.getInt("PaletteMax");
        this.root = root;
        this.width = root.getInt("Width");
        this.length = root.getInt("Length");
        this.height = root.getInt("Height");
        this.blocks = new VarIntArray(width, length, height);
        this.palette = new SpongePaletteParser(registry).parse(root.getCompound("Palette"), maxId);
        blocks.readBytes(root.getBytes("BlockData"));
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
    public BlockState getState(int x, int y, int z) {
        return palette.getValue(blocks.get(indexOf(x, y, z)));
    }

    @Override
    public Tag<?> getData(String key) {
        return root.get(key);
    }
}
