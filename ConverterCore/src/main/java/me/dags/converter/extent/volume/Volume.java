package me.dags.converter.extent.volume;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.io.BlockReader;
import me.dags.converter.extent.io.BlockWriter;
import me.dags.converter.extent.io.DataReader;
import me.dags.converter.extent.io.DataWriter;

public interface Volume {

    int getWidth();

    int getHeight();

    int getLength();

    default int size() {
        return getWidth() * getHeight() * getLength();
    }

    default int indexOf(int x, int y, int z) {
        int index = y * getWidth() * getLength();
        index += z * getWidth();
        index += x;
        return index;
    }

    interface Reader extends Volume, BlockReader, DataReader, Extent.Reader {

        @Override
        default void iterate(Extent.Visitor visitor) {
            for (int y = 0; y < getHeight(); y++) {
                for (int z = 0; z < getLength(); z++) {
                    for (int x = 0; x < getWidth(); x++) {
                        BlockState state = getState(x, y, z);
                        visitor.visit(x, y, z, state);
                    }
                }
            }
        }
    }

    interface Writer extends Volume, BlockWriter, DataWriter, Extent.Writer {

    }
}
