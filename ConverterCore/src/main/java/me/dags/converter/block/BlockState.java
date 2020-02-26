package me.dags.converter.block;

import me.dags.converter.block.fixer.None;
import me.dags.converter.block.fixer.StateFixer;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.registry.Registry;
import me.dags.converter.registry.RegistryItem;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.text.ParseException;

public class BlockState implements RegistryItem {

    public static final int MAX_ID = getStateId(4096, 15);
    public static final BlockState AIR = new BlockState(0, Nbt.compound(1).put("Name", "minecraft:air"), false);

    private final int stateId;
    private final boolean upgrade;
    private final CompoundTag data;
    private final String blockName;
    private final String identifier;
    private final StateFixer fixer;

    public BlockState(CompoundTag data) {
        this(0, data, false);
    }

    public BlockState(String name, String properties) throws ParseException {
        this.stateId = 0;
        this.upgrade = false;
        this.blockName = name;
        this.data = Serializer.deserialize(name, properties);
        this.identifier = Serializer.serialize(data);
        this.fixer = None.NONE;
    }

    public BlockState(int stateId, CompoundTag data, boolean upgrade) {
        this(stateId, data, StateFixer.NONE, upgrade);
    }

    public BlockState(int stateId, CompoundTag data, StateFixer fixer, boolean upgrade) {
        this.data = data;
        this.fixer = fixer;
        this.upgrade = upgrade;
        this.stateId = stateId;
        this.blockName = data.getString("Name");
        this.identifier = Serializer.serialize(data);
    }

    public BlockState getActualState(Registry.Parser<BlockState> parser, Chunk.Reader chunk, int x, int y, int z) throws Exception {
        return fixer.getActualState(this, parser, chunk, x, y, z);
    }

    public boolean isAir() {
        return getBlockId(getId()) == getBlockId(AIR.getId());
    }

    public boolean requiresUpgrade() {
        return upgrade;
    }

    public CompoundTag getData() {
        return data;
    }

    public String getBlockName() {
        return blockName;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int getId() {
        return stateId;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == BlockState.class && obj.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return "BlockState(" + getIdentifier() + ")";
    }

    public static int getStateId(int blockId, int meta) {
        return blockId + (meta << 12);
    }

    public static int getBlockId(int stateId) {
        return stateId & 4095;
    }

    public static int getMetaData(int stateId) {
        return (stateId >> 12) & 15;
    }
}
