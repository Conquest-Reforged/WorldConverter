package me.dags.converter.extent;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.io.BlockWriter;
import me.dags.converter.extent.io.DataReader;
import me.dags.converter.extent.io.DataWriter;

public interface Extent {

    interface Reader extends DataReader {

        void iterate(Visitor visitor) throws Exception;
    }

    interface Writer extends BlockWriter, DataWriter {

    }

    interface Visitor {

        void visit(int x, int y, int z, BlockState state);
    }
}
