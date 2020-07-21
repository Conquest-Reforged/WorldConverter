package me.dags.converter.data.entity.painting;

import me.dags.converter.converter.DataConverter;
import me.dags.converter.data.EntityDataConverter;
import me.dags.converter.util.Utils;

import java.util.List;

public class PaintingConverter extends EntityDataConverter {

    private static final List<DataConverter> fields = Utils.listOf(new MotiveConverter());

    public PaintingConverter() {
        super(fields);
    }

    @Override
    public String getId() {
        return "minecraft:painting";
    }
}
