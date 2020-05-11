package me.dags.converter.tile;

import me.dags.converter.block.BlockState;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.registry.RemappingRegistry;
import me.dags.converter.tile.chisel.ChiselTileConverter;
import me.dags.converter.tile.chisel.ChiselTileLegacy;
import me.dags.converter.util.log.Logger;

import java.util.Collections;
import java.util.List;

public class TileHelper {

    public static DataConverter getTileConverter(RemappingRegistry<BlockState> registry) {
        Logger.log("Adding tile converter");
        return new TileEntityConverter(getConverters(registry));
    }

    public static List<TileConverter> getConverters(RemappingRegistry<BlockState> registry) {
        return Collections.singletonList(getChiselConverter(registry));
    }

    public static TileConverter getChiselConverter(RemappingRegistry<BlockState> registry) {
        return ChiselTileConverter.builder()
                .states(registry)
                .reader(ChiselTileLegacy::new)
                .writer(ChiselTileLegacy::new)
                .build();
    }
}
