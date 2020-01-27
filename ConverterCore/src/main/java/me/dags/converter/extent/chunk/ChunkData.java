package me.dags.converter.extent.chunk;

import me.dags.converter.biome.convert.BiomeConverter;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.data.GameData;
import me.dags.converter.version.Version;
import org.jnbt.Nbt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ChunkData {

    public static List<DataConverter> sectionData() {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create(true, "BlockLight", "BlockLight"));
        list.add(DataConverter.create(true, "SkyLight", "SkyLight"));
        return list;
    }

    public static List<DataConverter> getLevelDataConverters(long seed, Version from, Version to, GameData data) {
        if (from.getId() < to.getId()) {
            return ChunkData.upgrade(seed, from, to, data);
        }
        if (from.getId() > to.getId()) {
            return ChunkData.downgrade(seed, from, to, data);
        }
        return ChunkData.transfer(seed, from, to, data);
    }

    private static List<DataConverter> upgrade(long seed, Version from, Version to, GameData gameData) {
        if (from.isLegacy()) {
            return upgradeFromLegacy(seed, from, to, gameData);
        }
        return transfer(seed, from, to, gameData);
    }

    private static List<DataConverter> downgrade(long seed, Version from, Version to, GameData gameData) {
        if (to.isLegacy()) {
            return downgradeToLegacy(seed, from, to, gameData);
        }
        return transfer(seed, from, to, gameData);
    }

    private static List<DataConverter> transfer(long seed, Version from, Version to, GameData gameData) {
        if (to.isLegacy()) {
            return transferLegacy(seed, from, to, gameData);
        }
        return transferLatest(seed, from, to, gameData);
    }

    // upgrade chunk data from Legacy names to Current
    private static List<DataConverter> upgradeFromLegacy(long seed, Version from, Version to, GameData gameData) {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("LightPopulated", "isLightOn"));
        list.add(DataConverter.create("TerrainPopulated", "Status", t -> Nbt.tag("full")));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(new BiomeConverter(seed, from, to, gameData.biomes));
        return Collections.unmodifiableList(list);
    }

    // downgrade chunk data from Current to legacy
    private static List<DataConverter> downgradeToLegacy(long seed, Version from, Version to, GameData gameData) {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("isLightOn", "LightPopulated"));
        list.add(DataConverter.create("Status", "TerrainPopulated", t -> Nbt.tag((byte) 1)));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(new BiomeConverter(seed, from, to, gameData.biomes));
        return Collections.unmodifiableList(list);
    }

    private static List<DataConverter> transferLegacy(long seed, Version from, Version to, GameData gameData) {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("LightPopulated", "LightPopulated"));
        list.add(DataConverter.create("TerrainPopulated", "TerrainPopulated"));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(new BiomeConverter(seed, from, to, gameData.biomes));
        return Collections.unmodifiableList(list);
    }

    private static List<DataConverter> transferLatest(long seed, Version from, Version to, GameData gameData) {
        List<DataConverter> list = new LinkedList<>();
        list.add(DataConverter.create("Entities", "Entities"));
        list.add(DataConverter.create("TileEntities", "TileEntities"));
        list.add(DataConverter.create("InhabitedTime", "InhabitedTime"));
        list.add(DataConverter.create("LastUpdate", "LastUpdate"));
        list.add(DataConverter.create("isLightOn", "isLightOn"));
        list.add(DataConverter.create("Status", "Status"));
        list.add(DataConverter.create("xPos", "xPos"));
        list.add(DataConverter.create("zPos", "zPos"));
        list.add(new BiomeConverter(seed, from, to, gameData.biomes));
        return Collections.unmodifiableList(list);
    }
}
