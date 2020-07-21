package me.dags.converter.data.entity;

import me.dags.converter.converter.DataConverter;
import me.dags.converter.data.EntityListConverter;
import me.dags.converter.data.entity.painting.PaintingConverter;
import me.dags.converter.util.Utils;

public class EntityConverters {

    public static DataConverter getDataConverter() {
        return new EntityListConverter("Entities", Utils.listOf(new PaintingConverter()));
    }
}
