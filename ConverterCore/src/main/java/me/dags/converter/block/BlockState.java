package me.dags.converter.block;

import me.dags.converter.registry.RegistryItem;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.text.ParseException;

public class BlockState implements RegistryItem {

    public static final int MAX_ID = getStateId(4096, 15);
    public static final BlockState AIR = new BlockState(0, Nbt.compound(1).put("Name", "minecraft:air"));

    private final int stateId;
    private final CompoundTag data;
    private final String blockName;
    private final String identifier;

    public BlockState(CompoundTag data) {
        this(0, data);
    }

    public BlockState(String name, String properties) throws ParseException {
        this.stateId = 0;
        this.data = Serializer.deserialize(name, properties);
        this.blockName = name;
        this.identifier = Serializer.serialize(data);
    }

    public BlockState(int stateId, CompoundTag data) {
        this.data = data;
        this.stateId = stateId;
        this.blockName = data.getString("Name");
        this.identifier = Serializer.serialize(data);
    }

    public boolean isAir() {
        return this == AIR;
    }

    public CompoundTag getData() {
        return data;
    }

    public int getStateId() {
        return stateId;
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
        return getStateId();
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
