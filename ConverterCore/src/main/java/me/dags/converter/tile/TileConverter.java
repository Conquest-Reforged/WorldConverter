package me.dags.converter.tile;

import org.jnbt.CompoundTag;

public interface TileConverter {

    CompoundTag convert(CompoundTag tile);

    static CompoundTag copyMissingData(CompoundTag from, CompoundTag to) {
        from.forEach(e -> {
            if (to.get(e.getKey()).isPresent()) {
                return;
            }
            to.put(e.getKey(), e.getValue());
        });
        return to;
    }
}
