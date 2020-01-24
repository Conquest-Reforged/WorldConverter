package me.dags.converter.block.registry;

import me.dags.converter.block.BlockState;
import me.dags.converter.block.Serializer;
import me.dags.converter.registry.AbstractRegistry;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.IntMap;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.text.ParseException;
import java.util.List;

public class BlockRegistry extends AbstractRegistry<BlockState> implements Registry.Parser<BlockState> {

    private BlockRegistry(AbstractRegistry.Builder<BlockState> builder) {
        super(builder);
    }

    @Override
    public Parser<BlockState> getParser() {
        return this;
    }

    @Override
    public BlockState parse(String in) throws ParseException {
        CompoundTag data = Serializer.deserialize(in);
        return parse(data);
    }

    @Override
    public BlockState parse(CompoundTag in) throws ParseException {
        return parse(new BlockState(in));
    }

    @Override
    public Reader<BlockState> parsePalette(List<Tag<CompoundTag>> list) throws ParseException {
        IntMap<BlockState> map = new IntMap<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            BlockState state = parse(list.get(i).asCompound());
            map.put(i, state);
        }
        return new PaletteReader<>(map, BlockState.AIR);
    }

    @Override
    public String getIdentifier(BlockState blockState) {
        int id = BlockState.getBlockId(blockState.getStateId());
        int meta = BlockState.getMetaData(blockState.getStateId());
        return blockState.getIdentifier() + "(" + id + ":" + meta + ")";
    }

    public static Builder<BlockState> builder(String version) {
        return new AbstractRegistry.Builder<>(version, BlockState.AIR, BlockState.MAX_ID, BlockRegistry::new);
    }
}
