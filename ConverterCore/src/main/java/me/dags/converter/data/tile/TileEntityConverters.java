package me.dags.converter.data.tile;

import me.dags.converter.block.BlockState;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.data.EntityConverter;
import me.dags.converter.data.EntityListConverter;
import me.dags.converter.data.tile.chisel.ChiselTileConverter;
import me.dags.converter.data.tile.chisel.ChiselTileLegacy;
import me.dags.converter.registry.RemappingRegistry;
import me.dags.converter.util.log.Logger;

import java.util.Collections;
import java.util.List;

// unused
public class TileEntityConverters {

    public static DataConverter getTileConverter(RemappingRegistry<BlockState> registry) {
        Logger.log("Adding tile converter");
        return new EntityListConverter("TileEntities", getConverters(registry));
    }

    public static List<EntityConverter> getConverters(RemappingRegistry<BlockState> registry) {
        return Collections.singletonList(getChiselConverter(registry));
    }

    public static EntityConverter getChiselConverter(RemappingRegistry<BlockState> registry) {
        return ChiselTileConverter.builder()
                .states(registry)
                .reader(ChiselTileLegacy::new)
                .writer(ChiselTileLegacy::new)
                .build();
    }
}
