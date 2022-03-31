package me.dags.converter.data;

import me.dags.converter.converter.DataConverter;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.ArrayList;
import java.util.List;

// converts a list of entities
public class EntityListConverter implements DataConverter {

    private final String inputKey;
    private final String outputKey;
    private final List<EntityConverter> converters;

    public EntityListConverter(String key, List<EntityConverter> converters) {
        this(key, key, converters);
    }

    public EntityListConverter(String inputKey, String outputKey, List<EntityConverter> converters) {
        this.inputKey = inputKey;
        this.outputKey = outputKey;
        this.converters = converters;
    }

    @Override
    public boolean isOptional() {
        return true;
    }

    @Override
    public String getInputKey() {
        return inputKey;
    }

    @Override
    public String getOutputKey() {
        return outputKey;
    }

    @Override
    public Tag<?> convert(Tag<?> tag) {
        ListTag<CompoundTag> list = tag.asList(TagType.COMPOUND);
        if (list.isAbsent()) {
            return tag;
        }

        List<CompoundTag> dataList = new ArrayList<>(list.size());
        for (CompoundTag child : list) {
            String id = child.getString("id");
            CompoundTag data = child;
            for (EntityConverter converter : converters) {
                if (data == null) {
                    break;
                }
                if (converter.getId().isEmpty() || converter.getId().equals(id)) {
                    data = converter.convert(data);
                }
            }
            dataList.add(data);
        }

        return Nbt.list(TagType.COMPOUND, dataList);
    }
}
