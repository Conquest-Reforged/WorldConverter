package me.dags.converter.tile.chisel;

import me.dags.converter.block.BlockState;
import me.dags.converter.registry.RemappingRegistry;
import me.dags.converter.tile.TileConverter;
import org.jnbt.CompoundTag;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ChiselTileConverter implements TileConverter {

    private final Function<String, String> idMapper;
    private final RemappingRegistry<BlockState> stateRegistry;
    private final BiFunction<CompoundTag, RemappingRegistry<BlockState>, ChiselTile.Reader> readerFactory;
    private final Function<RemappingRegistry<BlockState>, ChiselTile.Writer> writerFactory;

    public ChiselTileConverter(Builder builder) {
        this.idMapper = builder.idMapper;
        this.stateRegistry = builder.registry;
        this.readerFactory = builder.reader;
        this.writerFactory = builder.writer;
    }

    @Override
    public CompoundTag convert(CompoundTag tile) {
        ChiselTile.Reader reader = readerFactory.apply(tile, stateRegistry);
        if (reader.isValid()) {
            String tileId = convertTileId(reader.getTileId());
            BlockState[] states = convertStates(reader.getStateArray());

            ChiselTile.Writer writer = writerFactory.apply(stateRegistry);
            writer.setTileId(tileId);
            writer.setStateArray(states);

            // copy any tile data not already written to the writer
            return TileConverter.copyMissingData(tile, writer.getRoot());
        }
        return tile;
    }

    private String convertTileId(String id) {
        return idMapper.apply(id);
    }

    private BlockState[] convertStates(BlockState[] states) {
        BlockState[] out = new BlockState[states.length];
        for (int i = 0; i < states.length; i++) {
            out[i] = stateRegistry.getOutput(states[i]);
        }
        return out;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private BiFunction<CompoundTag, RemappingRegistry<BlockState>, ChiselTile.Reader> reader;
        private Function<RemappingRegistry<BlockState>, ChiselTile.Writer> writer;
        private Function<String, String> idMapper = s -> s;
        private RemappingRegistry<BlockState> registry;

        public Builder reader(BiFunction<CompoundTag, RemappingRegistry<BlockState>, ChiselTile.Reader> reader) {
            this.reader = reader;
            return this;
        }

        public Builder writer(Function<RemappingRegistry<BlockState>, ChiselTile.Writer> writer) {
            this.writer = writer;
            return this;
        }

        public Builder ids(Map<String, String> ids) {
            return ids(id -> ids.getOrDefault(id, id));
        }

        public Builder ids(Function<String, String> idMapper) {
            this.idMapper = idMapper;
            return this;
        }

        public Builder states(RemappingRegistry<BlockState> registry) {
            this.registry = registry;
            return this;
        }

        public ChiselTileConverter build() {
            return new ChiselTileConverter(this);
        }
    }
}
