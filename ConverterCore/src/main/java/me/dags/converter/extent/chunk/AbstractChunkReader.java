package me.dags.converter.extent.chunk;

import me.dags.converter.block.BlockState;
import me.dags.converter.data.tile.LazyTileEntityMap;
import me.dags.converter.data.tile.TileEntityMap;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.util.log.Logger;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.Arrays;
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
        this.sections = new Volume.Reader[17];	// Include room for y=-1
    }

    @Override
    public int getSectionCount() {
        return (sections.length - 1);
    }

    @Override
    public TileEntityMap getTileEntityMap() {
        return tileEntityMap;
    }

    @Override
    public Volume.Reader getSection(int index) throws Exception {
        Volume.Reader section = sections[index+1];
        if (section == null) {
            // Initialize all section readers
            for (int i = 0; i < this.sections.length; i++) {
                this.sections[i] = new EmptySection();
            }
            // Don't assume packed
            for (Tag<CompoundTag> s : this.sectionData) {
                CompoundTag sec = s.asCompound();
                int y = sec.getByteTag("Y").getValue();
                if ((y < -1) || (y >= 16)) continue;
                try {
                    this.sections[y+1] = createSection(sec);
                } catch (Exception x) {
                    Logger.log("Error handling section of chunk", x);
                }
            }
            section = sections[index+1];
        }
        return section;
    }

    @Override
    public Tag<?> getData(String key) {
        return level.get(key);
    }

    protected abstract Volume.Reader createSection(CompoundTag section) throws Exception;

    private static class EmptySection implements Volume.Reader {
        private EmptySection() {
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public int getLength() {
            return 16;
        }

        @Override
        public BlockState getState(int x, int y, int z) {
            return BlockState.AIR;
        }

        private static byte[] emptylight = new byte[2048];
        private static byte[] emptysky;
        @Override
        public Tag<?> getData(String key) {
            if (key.equals("BlockLight")) {
                return Nbt.tag(emptylight);
            }
            else if (key.equals("SkyLight")) {
                if (emptysky == null) { emptysky = new byte[2048]; Arrays.fill(emptysky, (byte)0xFF); }
                return Nbt.tag(emptylight);
            }
            Logger.log("EmptySection.getData(" + key + ")");
            return Nbt.list(TagType.COMPOUND, (Iterable<CompoundTag>) null);
        }
    }
}
