package me.dags.converter.extent.chunk;

import me.dags.converter.data.tile.LazyTileEntityMap;
import me.dags.converter.data.tile.TileEntityMap;
import me.dags.converter.extent.volume.Volume;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.List;

public abstract class AbstractChunkReader implements Chunk.Reader {

    private final CompoundTag level;
    private final Volume.Reader[] sections;
    private final TileEntityMap tileEntityMap;
    private final List<Tag<CompoundTag>> sectionData;

    public AbstractChunkReader(CompoundTag root) {
        this.level = root.getCompound("Level");
        this.tileEntityMap = new LazyTileEntityMap(level);
        this.sectionData = level.getListTag("Sections", TagType.COMPOUND).getBacking();
        this.sections = new Volume.Reader[sectionData.size()];
    }

    @Override
    public int getSectionCount() {
        return sections.length;
    }

    @Override
    public TileEntityMap getTileEntityMap() {
        return tileEntityMap;
    }

    @Override
    public Volume.Reader getSection(int index) throws Exception {
        Volume.Reader reader = sections[index];
        if (reader == null) {
            reader = createSection(sectionData.get(index).asCompound());
            sections[index] = reader;
        }
        return reader;
    }

    @Override
    public Tag<?> getData(String key) {
        return level.get(key);
    }

    protected abstract Volume.Reader createSection(CompoundTag section) throws Exception;
}
