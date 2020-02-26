package me.dags.converter.extent.structure;

import com.google.gson.JsonObject;
import me.dags.converter.block.BlockState;
import me.dags.converter.block.registry.PaletteWriter;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.LinkedList;
import java.util.List;

public class StructureWriter implements Structure.Writer {

    private final StructureConfig config;
    private final List<CompoundTag> blocks;
    private final Registry.Writer<BlockState> palette;
    private final CompoundTag root = Nbt.compound();

    private int minX, minY, minZ;
    private int maxX, maxY, maxZ;

    public StructureWriter(WriterConfig config) {
        this.config = StructureConfig.parse(new JsonObject());
        this.palette = new PaletteWriter<>();
        this.blocks = new LinkedList<>();
    }

    @Override
    public void setState(int x, int y, int z, BlockState state) {
        if (config.filter.test(state)) {
            return;
        }
        if (state.getBlockName().equals("minecraft:air")) {
            return;
        }
        CompoundTag block = Nbt.compound();
        block.put("pos", StructureHelper.vec(x, y, z));
        block.put("state", palette.getOrCreateId(state));
        blocks.add(block);
        recordPos(x, y, z);
    }

    @Override
    public void setData(String key, Tag<?> data) {
        root.put(key.toLowerCase(), data);
    }

    @Override
    public CompoundTag flush() {
        root.put("palette", buildPalette());
        root.put("entities", buildEntities());
        root.put("blocks", buildBlocks());
        root.put("size", buildSize());
        root.put("version", 1);
        if (config.relativize) {
            return StructureHelper.relativize(root, config);
        }
        return root;
    }

    private void recordPos(int x, int y, int z) {
        minX = Math.min(x, minX);
        minY = Math.min(y, minY);
        minZ = Math.min(z, minZ);
        maxX = Math.max(x, maxX);
        maxY = Math.max(y, maxY);
        maxZ = Math.max(z, maxZ);
    }

    private ListTag<CompoundTag> buildPalette() {
        ListTag<CompoundTag> list = Nbt.list(TagType.COMPOUND);
        for (BlockState state : palette) {
            list.add(state.getData());
        }
        return Nbt.list(TagType.COMPOUND, list);
    }

    private ListTag<CompoundTag> buildEntities() {
        return Nbt.list(TagType.COMPOUND, (Iterable<CompoundTag>) null);
    }

    private ListTag<CompoundTag> buildBlocks() {
        return Nbt.list(TagType.COMPOUND, blocks);
    }

    private ListTag<Integer> buildSize() {
        return StructureHelper.vec(1 + (maxX - minX), 1 + (maxY - minY), 1 + (maxZ - minZ));
    }


}
