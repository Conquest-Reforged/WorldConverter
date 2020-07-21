package me.dags.converter.data.tile;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.TagType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LazyTileEntityMap implements TileEntityMap {

    private static final int SIZE = 16;
    private static final int AREA = SIZE * SIZE;

    private final CompoundTag level;

    private Map<Integer, CompoundTag> entities = null;

    public LazyTileEntityMap(CompoundTag level) {
        this.level = level;
    }

    @Override
    public CompoundTag getEntity(int x, int y, int z) {
        buildMap();
        return entities.get(index(x, y, z));
    }

    private void buildMap() {
        if (entities == null) {
            ListTag<CompoundTag> list = level.getListTag("TileEntities", TagType.COMPOUND);
            if (list.isPresent() && list.size() > 0) {
                entities = new HashMap<>();
                for (CompoundTag entity : list) {
                    int x = entity.getInt("x");
                    int y = entity.getInt("y");
                    int z = entity.getInt("z");
                    int seed = index(x, y, z);
                    entities.put(seed, entity);
                }
            } else {
                entities = Collections.emptyMap();
            }
        }
    }

    private static int index(int x, int y, int z) {
        return (y * AREA) + (z * SIZE) + x;
    }
}
