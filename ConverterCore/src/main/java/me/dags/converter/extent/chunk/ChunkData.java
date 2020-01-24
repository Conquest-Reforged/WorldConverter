package me.dags.converter.extent.chunk;

import me.dags.converter.biome.Biome;
import me.dags.converter.biome.convert.BiomeConverter;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.data.GameData;
import me.dags.converter.registry.Registry;
import me.dags.converter.version.Version;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.TagType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ChunkData {

    public static DataConverter biomeConverter(Registry<Biome> registry) {
        return DataConverter.create("Biomes", "Biomes", tag -> {
            byte[] in = tag.asByteArray().getValue();
            byte[] out = new byte[in.length];
            for (int i = 0; i < in.length; i++) {
                Biome biome = registry.getValue(in[i]);
                out[i] = (byte) registry.getId(biome);
            }
            return Nbt.tag(out);
        });
    }

    public static List<DataConverter> latestLevel() {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("HeightMaps", "HeightMaps"));
        list.add(DataConverter.create("Structures", "Structures"));
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("LiquidTicks", "LiquidTicks"));
        list.add(DataConverter.create("PostProcessing", "PostProcessing"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("TileTicks", "TileTicks"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("isLightOn", "isLightOn"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("Status", "Status"));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(DataConverter.create("Biomes", "Biomes"));
        return Collections.unmodifiableList(list);
    }

    public static List<DataConverter> legacyLevel() {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("LightPopulated", "LightPopulated"));
        list.add(DataConverter.create("TerrainPopulated", "TerrainPopulated"));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(DataConverter.create("Biomes", "Biomes"));
        return Collections.unmodifiableList(list);
    }

    public static List<DataConverter> legacyToLatestLevel(Version from, Version to, GameData gameData) {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("LightPopulated", "isLightOn"));
        list.add(DataConverter.create("TerrainPopulated", "Status", t -> Nbt.tag("full")));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(new BiomeConverter(from, to, gameData.biomes));
        return Collections.unmodifiableList(list);
    }

    public static List<DataConverter> latestLegacyLevel(Version from, Version to, GameData gameData) {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("isLightOn", "LightPopulated"));
        list.add(DataConverter.create("Status", "TerrainPopulated", t -> Nbt.tag((byte) 1)));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(new BiomeConverter(from, to, gameData.biomes));
        return Collections.unmodifiableList(list);
    }

    public static List<DataConverter> v114Tov115(Version from, Version to, GameData gameData) {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("CarvingMasks", "CarvingMasks"));
        list.add(DataConverter.create("Heightmaps", "Heightmaps"));
        list.add(DataConverter.create("Structures", "Structures"));
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("Lights", "Lights"));
        list.add(DataConverter.create("LiquidsToBeTicked", "LiquidsToBeTicked"));
        list.add(DataConverter.create("PostProcessing", "PostProcessing"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("ToBeTicked", "ToBeTicked"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("Status", "Status"));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(new BiomeConverter(from, to, gameData.biomes));
        return Collections.unmodifiableList(list);
    }

    public static List<DataConverter> sectionData() {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create(true, "BlockLight", "BlockLight"));
        list.add(DataConverter.create(true, "SkyLight", "SkyLight"));
        return list;
    }

    private static Supplier<ListTag<CompoundTag>> compoundList() {
        return () -> Nbt.list(TagType.COMPOUND, Collections.emptyList());
    }
}
