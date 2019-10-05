package me.dags.converter.extent.schematic.latest;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.latest.AbstractVolumeReader;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;

import java.text.ParseException;

public class SchematicReader extends AbstractVolumeReader {

    private final int width;
    private final int height;
    private final int length;

    public SchematicReader(Registry<BlockState> registry, CompoundTag root) throws ParseException {
        super(registry, root);
        this.width = root.getInt("Width");
        this.length = root.getInt("Length");
        this.height = root.getInt("Height");
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }
}
