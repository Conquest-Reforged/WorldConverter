package me.dags.converter.block.fixer;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;
import org.jnbt.StringTag;

public class DoublePlant implements StateFixer {
    @Override
    public BlockState getActualState(BlockState state, Registry.Parser<BlockState> parser, Chunk.Reader chunk, int x, int y, int z) throws Exception {
        CompoundTag propertiesIn = state.getData().getCompound("Properties");
        StringTag half = propertiesIn.getStringTag("half");

        if (half.getValue().equals("upper")) {
            BlockState lower = chunk.getState(x, y - 1, z);
            if (state.getBlockName().equals(lower.getBlockName())) {
                StringTag variant = state.getData().getStringTag("variant");

                CompoundTag propertiesOut = Nbt.compound(3);
                propertiesOut.put("half", half);
                propertiesOut.put("facing", propertiesIn.getStringTag("facing"));
                propertiesOut.put("variant", variant);

                return parser.parse(
                        Nbt.compound(2)
                                .put("Name", state.getBlockName())
                                .put("Properties", propertiesOut)
                );
            }
        }

        return state;
    }
}
