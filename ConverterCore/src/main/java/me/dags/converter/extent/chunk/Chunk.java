package me.dags.converter.extent.chunk;

import me.dags.converter.block.BlockState;
import me.dags.converter.data.tile.TileEntityMap;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.io.DataReader;
import me.dags.converter.extent.io.DataWriter;
import me.dags.converter.extent.volume.Volume;

public interface Chunk {

    interface Reader extends Extent.Reader, DataReader {

        int getSectionCount();

        Volume.Reader getSection(int index) throws Exception;

        TileEntityMap getTileEntityMap();

        default BlockState getState(int x, int y, int z) throws Exception {
            Volume.Reader section = getSection(y >> 4);
            return section.getState(x & 15, y & 15, z & 15);
        }

        @Override
        default void iterate(Extent.Visitor visitor) throws Exception {
            for (int i = 0; i < getSectionCount(); i++) {
                int startY = i << 4;
                Volume.Reader section = getSection(i);
                section.iterate((x, y, z, state) -> visitor.visit(x, startY + y, z, state));
            }
        }
    }

    interface Writer extends Extent.Writer, DataWriter {

        void markUpgrade(int section, int dx, int dy, int dz);

        Volume.Writer getSection(int index);

        default void setState(int x, int y, int z, BlockState state) {
            if (true) throw new RuntimeException("PLEASE NO");
            int index = y >> 4;
            getSection(index).setState(x, y, z, state);
        }
    }
}
