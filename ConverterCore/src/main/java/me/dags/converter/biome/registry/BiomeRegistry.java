package me.dags.converter.biome.registry;

import me.dags.converter.biome.Biome;
import me.dags.converter.block.registry.PaletteReader;
import me.dags.converter.registry.AbstractRegistry;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.IntMap;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.text.ParseException;
import java.util.List;

public class BiomeRegistry extends AbstractRegistry<Biome> implements Registry.Parser<Biome> {

    protected BiomeRegistry(Builder<Biome> builder) {
        super(builder);
    }

    @Override
    public Parser<Biome> getParser() {
        return this;
    }

    @Override
    public Biome parse(String in) throws ParseException {
        return parse(new Biome(in, -1));
    }

    @Override
    public Biome parse(CompoundTag in) throws ParseException {
        return parse(new Biome(in.getString("K"), in.getInt("V")));
    }

    @Override
    public Reader<Biome> parsePalette(List<Tag<CompoundTag>> list) throws ParseException {
        IntMap<Biome> map = new IntMap<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Biome biome = parse(list.get(i).asCompound());
            map.put(i, biome);
        }
        return new PaletteReader<>(map, Biome.NONE);
    }

    public static Builder<Biome> builder(String version) {
        return new AbstractRegistry.Builder<>(version, Biome.NONE, Biome.MAX_ID, BiomeRegistry::new);
    }
}
