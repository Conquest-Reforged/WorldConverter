package me.dags.converter.block.extender;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.util.Objects;

public class SimpleTileStateExtender implements StateExtender {

    private final String identifier;
    private final String[] keys;

    public SimpleTileStateExtender(String identifier, String... keys) {
        this.identifier = identifier;
        this.keys = keys;
    }

    @Override
    public BlockState getExtendedState(BlockState state, Registry.Parser<BlockState> parser, Chunk.Reader chunk, int x, int y, int z) {
        // check for tile data at xyz
        CompoundTag tileData = chunk.getTileEntityMap().getEntity(x, y, z);
        if (tileData.isAbsent()) {
            return state;
        }

        // ensure data is for the expected type
        String id = tileData.getString("id");
        if (!id.equals(identifier)) {
            return state;
        }

        // get the states 'regular' properties
        CompoundTag properties = state.getData().getCompound("Properties");
        if (properties.isAbsent()) {
            return state;
        }

        // create a copy of the properties so we're not modifying the original
        CompoundTag extendedProperties = properties.copy();

        // search the tileData for the expected entries and copy to extendedProperties
        for (String key : keys) {
            Tag<?> tag = tileData.get(key);
            if (tag.isPresent()) {
                String value = toString(key, tag);
                // tileData properties denoted by #
                extendedProperties.put("#" + key, value);
            }
        }

        // create a new blockstate instance from the original but with extended properties
        return BlockState.createTransientInstance(state, extendedProperties);
    }

    protected String toString(String name, Tag<?> tag) {
        return Objects.toString(tag.getValue());
    }
}
