package me.dags.converter.extent.chunk;

import me.dags.converter.extent.volume.Volume;
import me.dags.converter.version.Version;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractChunkWriter implements Chunk.Writer {

    private int top = -1;
    private final Version version;
    private final CompoundTag level = Nbt.compound();
    private final Volume.Writer[] sections = new Volume.Writer[16];

    protected AbstractChunkWriter(Version version) {
        this.version = version;
    }

    @Override
    public Volume.Writer getSection(int index) {
        Volume.Writer section = sections[index];
        if (section == null) {
            section = createSection(index);
            sections[index] = section;
            top = Math.max(top, index);
        }
        return section;
    }

    @Override
    public void setData(String key, Tag<?> data) {
        level.put(key, data);
    }

    @Override
    public CompoundTag flush() {
        level.put("Sections", Nbt.list(TagType.COMPOUND, flushSections()));
        CompoundTag root = createRoot();
        root.put("Level", level);
        root.put("DataVersion", version.getId());
        return root;
    }

    private List<CompoundTag> flushSections() {
        List<CompoundTag> list = new LinkedList<>();
        addSections(list);
        return list;
    }

    protected void addSections(List<CompoundTag> list) {
        for (int i = 0; i <= top; i++) {
            Volume.Writer section = sections[i];
            if (section == null) {
                list.add(Nbt.compound().put("Y", i));
            } else {
                list.add(section.flush());
            }
        }
    }

    protected abstract CompoundTag createRoot();

    protected abstract Volume.Writer createSection(int index);
}
