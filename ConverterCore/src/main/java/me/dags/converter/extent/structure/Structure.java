package me.dags.converter.extent.structure;

import me.dags.converter.extent.Extent;
import me.dags.converter.extent.io.BlockWriter;
import me.dags.converter.extent.io.DataReader;
import me.dags.converter.extent.io.DataWriter;

public interface Structure {

    interface Reader extends Extent.Reader, DataReader {

    }

    interface Writer extends Extent.Writer, BlockWriter, DataWriter {

    }
}
