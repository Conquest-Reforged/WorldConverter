package me.dags.converter.converter;

import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;

public interface WriterFactory {

    Extent.Writer create(WriterConfig config) throws Exception;
}
