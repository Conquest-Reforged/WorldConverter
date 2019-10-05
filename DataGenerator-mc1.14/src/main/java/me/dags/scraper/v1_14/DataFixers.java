package me.dags.scraper.v1_14;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.fixes.BiomeRenames;

import java.util.HashMap;
import java.util.Map;

public class DataFixers {

    private static final Map<String, String> pottedPlants = new HashMap<>();

    static {
        pottedPlants.put("empty", Blocks.FLOWER_POT.getRegistryName() + "");
        pottedPlants.put("rose", Blocks.POTTED_POPPY.getRegistryName() + "");  // X
        pottedPlants.put("blue_orchid", Blocks.POTTED_BLUE_ORCHID.getRegistryName() + "");
        pottedPlants.put("allium", Blocks.POTTED_ALLIUM.getRegistryName() + "");
        pottedPlants.put("houstonia", Blocks.POTTED_AZURE_BLUET.getRegistryName() + ""); // ?
        pottedPlants.put("red_tulip", Blocks.POTTED_RED_TULIP.getRegistryName() + "");
        pottedPlants.put("orange_tulip", Blocks.POTTED_ORANGE_TULIP.getRegistryName() + "");
        pottedPlants.put("white_tulip", Blocks.POTTED_WHITE_TULIP.getRegistryName() + "");
        pottedPlants.put("pink_tulip", Blocks.POTTED_PINK_TULIP.getRegistryName() + "");
        pottedPlants.put("oxeye_daisy", Blocks.POTTED_OXEYE_DAISY.getRegistryName() + "");
        pottedPlants.put("dandelion", Blocks.POTTED_DANDELION.getRegistryName() + "");
        pottedPlants.put("oak_sapling", Blocks.POTTED_OAK_SAPLING.getRegistryName() + "");
        pottedPlants.put("spruce_sapling", Blocks.POTTED_SPRUCE_SAPLING.getRegistryName() + "");
        pottedPlants.put("birch_sapling", Blocks.POTTED_BIRCH_SAPLING.getRegistryName() + "");
        pottedPlants.put("jungle_sapling", Blocks.POTTED_JUNGLE_SAPLING.getRegistryName() + "");
        pottedPlants.put("acacia_sapling", Blocks.POTTED_ACACIA_SAPLING.getRegistryName() + "");
        pottedPlants.put("dark_oak_sapling", Blocks.POTTED_DARK_OAK_SAPLING.getRegistryName() + "");
        pottedPlants.put("mushroom_brown", Blocks.POTTED_BROWN_MUSHROOM.getRegistryName() + "");
        pottedPlants.put("dead_bush", Blocks.POTTED_DEAD_BUSH.getRegistryName() + "");
        pottedPlants.put("fern", Blocks.POTTED_FERN.getRegistryName() + "");
        pottedPlants.put("cactus", Blocks.POTTED_CACTUS.getRegistryName() + "");
        pottedPlants.put("mushroom_red", Blocks.POTTED_RED_MUSHROOM.getRegistryName() + "");
    }

    public static String fixBiome(String biome) {
        int oldVer = 1125;
        int newVer = SharedConstants.getVersion().getWorldVersion();
        System.out.println(biome + ": " + oldVer + " -> " + newVer);
        Dynamic<?> dynamic = new Dynamic<>(NBTDynamicOps.INSTANCE, new StringNBT(biome));
        dynamic = DataFixesManager.getDataFixer().update(TypeReferences.BIOME, dynamic, oldVer, newVer);
        StringNBT tag = (StringNBT) dynamic.getValue();
        return tag.getString();
    }

    public static CompoundNBT fixState(CompoundNBT state) {
        CompoundNBT fixed = customFix(state);
        if (fixed == state) {
            return fixOneState(state);
        }
        return fixed;
    }

    public static CompoundNBT fixOneState(CompoundNBT state) {
        int oldVer = 100;
        int newVer = SharedConstants.getVersion().getWorldVersion();
        Dynamic<?> dynamic = new Dynamic<>(NBTDynamicOps.INSTANCE, state);
        dynamic = DataFixesManager.getDataFixer().update(TypeReferences.BLOCK_STATE, dynamic, oldVer, newVer);
        CompoundNBT tag = (CompoundNBT) dynamic.getValue();
        BlockState blockState = NBTUtil.readBlockState(tag);
        return NBTUtil.writeBlockState(blockState);
    }

    public static boolean fixable(CompoundNBT state) {
        switch (state.getString("Name")) {
            case "minecraft:skull":
                return false;
            default:
                return true;
        }
    }

    private static CompoundNBT customFix(CompoundNBT in) {
        switch (in.getString("Name")) {
            case "minecraft:flower_pot":
                return fixPlantPot(in);
            case "minecraft:leaves":
                return fixLeaves(in);
            default:
                return in;
        }
    }

    private static CompoundNBT fixLeaves(CompoundNBT in) {
        CompoundNBT fixed = fixOneState(in);
        CompoundNBT from = in.getCompound("Properties");
        CompoundNBT to = fixed.getCompound("Properties");
        boolean persist = from.getString("decayable").equals("false");
        to.putString("persistent", String.valueOf(persist));
        return fixed;
    }

    private static CompoundNBT fixPlantPot(CompoundNBT in) {
        CompoundNBT props = in.getCompound("Properties");
        String contents = props.getString("contents");
        String flattened = pottedPlants.get(contents);
        CompoundNBT flat = new CompoundNBT();
        flat.putString("Name", flattened);
        return flat;
    }
}
