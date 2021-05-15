package me.dags.converter.data.tile;

import me.dags.converter.util.Utils;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.TagType;

import java.util.Collections;
import java.util.Map;

public class LazyTileEntityMap implements TileEntityMap {

    private static final CompoundTag EMPTY = Nbt.tag(Collections.emptyMap());

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
        return entities.getOrDefault(index(x, y, z), EMPTY);
    }

    private void buildMap() {
        if (entities == null) {
            ListTag<CompoundTag> list = level.getListTag("TileEntities", TagType.COMPOUND);
            if (list.isPresent() && list.size() > 0) {
                entities = Utils.newMap(list.size());
                for (CompoundTag entity : list) {
                    int x = entity.getInt("x");
                    int y = entity.getInt("y");
                    int z = entity.getInt("z");
                    entities.put(index(x, y, z), entity);
                }
            } else {
                entities = Collections.emptyMap();
            }
        }
    }

    private static int index(int x, int y, int z) {
        // make sure x & z are chunk-relative (ie 0-15)
        x &= 15;
        z &= 15;
        return (y * AREA) + (z * SIZE) + x;
    }
}
