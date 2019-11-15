package me.dags.converter.extent.schematic.sponge;

import me.dags.converter.block.BlockState;
import me.dags.converter.registry.Registry;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.text.ParseException;
import java.util.Map;

public class SpongePaletteParser {

    private final Registry<BlockState> registry;

    public SpongePaletteParser(Registry<BlockState> registry) {
        this.registry = registry;
    }

    public Registry.Reader<BlockState> parse(CompoundTag compound, int maxId) throws ParseException {
        BlockState[] palette = new BlockState[maxId];
        for (Map.Entry<String, Tag> e : compound) {
            palette[e.getValue().asInt().getValue()] = registry.getParser().parse(e.getKey());
        }
        return new SpongePalette<>(palette);
    }
}
