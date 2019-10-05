package me.dags.converter.extent.structure;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.Extent;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.text.ParseException;
import java.util.List;

public class StructureReader implements Structure.Reader {

    private final CompoundTag root;
    private final Registry.Reader<BlockState> palette;
    private final List<Tag<CompoundTag>> blocks;

    public StructureReader(Registry<BlockState> registry, CompoundTag root) throws ParseException {
        List<Tag<CompoundTag>> palette = root.getListTag("Palette", TagType.COMPOUND).getBacking();
        this.palette = registry.getParser().parsePalette(palette);
        this.blocks = root.getListTag("blocks", TagType.COMPOUND).getBacking();
        this.root = root;
    }

    @Override
    public Tag<?> getData(String key) {
        return root.get(key);
    }

    @Override
    public void iterate(Extent.Visitor visitor) {
        for (Tag<CompoundTag> block : blocks) {
            List<Tag<Integer>> pos = block.asCompound().getListTag("pos", TagType.INT).getBacking();
            int x = pos.get(0).getValue();
            int y = pos.get(1).getValue();
            int z = pos.get(2).getValue();
            int index = block.asCompound().getInt("state");
            BlockState state = palette.getVal(index);
            visitor.visit(x, y, z, state);
        }
    }
}
