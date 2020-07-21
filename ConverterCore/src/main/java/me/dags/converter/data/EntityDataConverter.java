package me.dags.converter.data;

import me.dags.converter.converter.DataConverter;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;

import java.util.List;

// converts the data of a given entity
public abstract class EntityDataConverter implements EntityConverter {

    private final List<DataConverter> fields;

    public EntityDataConverter(List<DataConverter> fields) {
        this.fields = fields;
    }

    @Override
    public CompoundTag convert(CompoundTag data) {
        CompoundTag output = Nbt.compound(data.getBacking().size());
        for (DataConverter converter : fields) {
            Tag<?> tag = data.get(converter.getInputKey());
            if (tag.isPresent()) {
                tag = converter.convert(tag);
            }
            output.put(converter.getOutputKey(), tag);
        }
        return EntityConverter.copyMissingData(data, output);
    }
}
