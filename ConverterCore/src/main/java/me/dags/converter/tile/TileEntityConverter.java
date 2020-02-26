package me.dags.converter.tile;

import me.dags.converter.converter.DataConverter;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.util.ArrayList;
import java.util.List;

public class TileEntityConverter implements DataConverter {

    private final String keyIn;
    private final String keyOut;
    private final List<TileConverter> tileConverters;

    public TileEntityConverter(List<TileConverter> converters) {
        this("TileEntities", "TileEntities", converters);
    }

    public TileEntityConverter(String keyIn, String keyOut, List<TileConverter> tileConverters) {
        this.keyIn = keyIn;
        this.keyOut = keyOut;
        this.tileConverters = tileConverters;
    }

    @Override
    public boolean isOptional() {
        return true;
    }

    @Override
    public String getInputKey() {
        return keyIn;
    }

    @Override
    public String getOutputKey() {
        return keyOut;
    }

    @Override
    public Tag<?> convert(Tag<?> tag) {
        ListTag<CompoundTag> tiles = tag.asList(TagType.COMPOUND);
        if (tiles.isAbsent()) {
            return tag;
        }

        List<CompoundTag> tilesOut = new ArrayList<>(tiles.size());
        for (CompoundTag tile : tiles) {
            CompoundTag tileOut = tile;
            for (TileConverter converter : tileConverters) {
                tileOut = converter.convert(tileOut);
            }
            tilesOut.add(tileOut);
        }

        if (tilesOut.isEmpty()) {
            return tiles;
        }

        return Nbt.list(TagType.COMPOUND, tilesOut);
    }
}
