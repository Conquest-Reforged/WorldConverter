package me.dags.converter.block.extender;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.StringTag;

public class DoorExtender implements StateExtender {
    @Override
    public BlockState getExtendedState(BlockState state, Registry.Parser<BlockState> parser, Chunk.Reader chunk, int x, int y, int z) throws Exception {
        CompoundTag properties = state.getData().getCompound("Properties");
        StringTag half = properties.getStringTag("half");
        if (half.getValue().equals("upper")) {
            BlockState lower = chunk.getState(x, y - 1, z);
            if (state.getBlockName().equals(lower.getBlockName())) {

                CompoundTag lowerProperties = lower.getData().getCompound("Properties");
                StringTag lowerFacing = lowerProperties.getStringTag("facing");
                StringTag lowerOpen = lowerProperties.getStringTag("open");
                CompoundTag extendedProperties = properties.copy();
                String keyFacing = "facing";
                String keyOpen = "open";

                switch (lowerFacing.getValue()) {
                    case "north":
                        extendedProperties.put("#" + keyFacing, "north");
                        break;
                    case "east":
                        extendedProperties.put("#" + keyFacing, "east");
                        break;
                    case "south":
                        extendedProperties.put("#" + keyFacing, "south");
                        break;
                    case "west":
                        extendedProperties.put("#" + keyFacing, "west");
                        break;
                }

                if (lowerOpen.getValue().equals("true")) {
                    extendedProperties.put("#" + keyOpen, "true");
                } else {
                    extendedProperties.put("#" + keyOpen, "false");
                }

                return BlockState.createTransientInstance(state, extendedProperties);
            }
        }
        return state;
    }
}
