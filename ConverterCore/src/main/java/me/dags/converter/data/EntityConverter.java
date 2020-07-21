package me.dags.converter.data;

import org.jnbt.CompoundTag;

public interface EntityConverter {

    String getId();

    CompoundTag convert(CompoundTag data);

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
