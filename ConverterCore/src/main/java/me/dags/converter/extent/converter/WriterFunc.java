package me.dags.converter.extent.converter;

import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;

public interface WriterFunc {

    Extent.Writer get(WriterConfig config) throws Exception;
}
