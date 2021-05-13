package me.dags.converter.block.extender;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;
import org.jnbt.StringTag;

public class DoublePlantExtender_v1_12 implements StateExtender {
    @Override
    public BlockState getExtendedState(BlockState state, Registry.Parser<BlockState> parser, Chunk.Reader chunk, int x, int y, int z) throws Exception {
        CompoundTag properties = state.getData().getCompound("Properties");
        StringTag half = properties.getStringTag("half");
        if (half.getValue().equals("upper")) {
            BlockState lower = chunk.getState(x, y - 1, z);
            if (state.getBlockName().equals(lower.getBlockName())) {

                CompoundTag lowerProperties = lower.getData().getCompound("Properties");
                StringTag lowerFlowerType = lowerProperties.getStringTag("variant");
                CompoundTag extendedProperties = properties.copy();
                String key = "flower";

                if (lowerFlowerType.getValue().equals("sunflower")) {
                    extendedProperties.put("#" + key, "sunflower");
                    return BlockState.createTransientInstance(state, extendedProperties);

                } else if (lowerFlowerType.getValue().equals("syringa")) {
                    extendedProperties.put("#" + key, "tall_lilac");
                    return BlockState.createTransientInstance(state, extendedProperties);

                } else if (lowerFlowerType.getValue().equals("double_rose")) {
                    extendedProperties.put("#" + key, "double_rose");
                    return BlockState.createTransientInstance(state, extendedProperties);

                } else if (lowerFlowerType.getValue().equals("paeonia")) {
                    extendedProperties.put("#" + key, "paeonia");
                    return BlockState.createTransientInstance(state, extendedProperties);

                } else if (lowerFlowerType.getValue().equals("double_grass")) {
                    extendedProperties.put("#" + key, "double_grass");
                    return BlockState.createTransientInstance(state, extendedProperties);

                } else if (lowerFlowerType.getValue().equals("double_fern")) {
                    extendedProperties.put("#" + key, "double_fern");
                    return BlockState.createTransientInstance(state, extendedProperties);

                }
            }
        }
        return state;
    }
}
