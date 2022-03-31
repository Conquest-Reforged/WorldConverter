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
    private boolean hasUpgrade;
    private final Version version;
    private final CompoundTag level = Nbt.compound();
    private final UpgradeData[] upgradeData = new UpgradeData[16];
    private final Volume.Writer[] sections = new Volume.Writer[16];

    protected AbstractChunkWriter(Version version) {
        this.version = version;
    }

    @Override
    public void markUpgrade(int section, int dx, int dy, int dz) {
        UpgradeData sectionData = upgradeData[section];
        if (sectionData == null) {
            hasUpgrade = true;
            sectionData = new UpgradeData();
            upgradeData[section] = sectionData;
        }
        sectionData.mark(dx, dy, dz);
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
        if (hasUpgrade) {
            level.put("UpgradeData", flushUpgradeData());
        }
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

    private CompoundTag flushUpgradeData() {
        CompoundTag root = Nbt.compound(1);
        CompoundTag indices = Nbt.compound(16);
        for (int i = 0; i < upgradeData.length; i++) {
            UpgradeData sectionData = upgradeData[i];
            if (sectionData == null) {
                continue;
            }
            indices.put(String.valueOf(i), sectionData.toArray());
        }
        root.put("Indices", indices);
        return root;
    }

    protected void addSections(List<CompoundTag> list) {
        for (int i = 0; i <= top; i++) {
            Volume.Writer section = sections[i];
            list.add(section.flush());
        }
    }

    protected abstract CompoundTag createRoot();

    protected abstract Volume.Writer createSection(int index);
}
