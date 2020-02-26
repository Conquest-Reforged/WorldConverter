package me.dags.scraper.v1_14;

import me.dags.converter.datagen.GameDataWriter;
import me.dags.converter.datagen.Schema;
import me.dags.converter.datagen.SectionWriter;
import me.dags.converter.datagen.biome.BiomeData;
import me.dags.converter.datagen.block.BlockData;
import me.dags.converter.datagen.block.StateData;
import me.dags.converter.version.versions.MinecraftVersion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod("data_generator")
public class Scraper {

    public Scraper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void generate(FMLCommonSetupEvent event) {
        Schema schema = Schema.modern("1.14");
        try (GameDataWriter writer = new GameDataWriter(schema)) {
            try (SectionWriter<BlockData> section = writer.startBlocks()){
                for (Block block : ForgeRegistries.BLOCKS) {
                    section.write(getBlockData(block));
                }
            }
            try (SectionWriter<BiomeData> section = writer.startBiomes()) {
                for (Biome biome : ForgeRegistries.BIOMES) {
                    section.write(geBiomeData(biome));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Mappings.generate();
    }

    private static BiomeData geBiomeData(Biome biome) {
        return new BiomeData(biome.getRegistryName(), Registry.BIOME.getId(biome));
    }

    private static BlockData getBlockData(Block block) {
        StateData defaults = getStateData(block.getDefaultState());
        List<StateData> states = new LinkedList<>();
        for (BlockState state : block.getStateContainer().getValidStates()) {
            states.add(getStateData(state));
        }
        return new BlockData(block.getRegistryName(), defaults,states);
    }

    private static StateData getStateData(BlockState state) {
        StringBuilder sb = new StringBuilder();
        state.getProperties().stream().sorted(Comparator.comparing(IProperty::getName)).forEach(p -> {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p.getName()).append('=').append(state.get(p).toString().toLowerCase());
        });
        return new StateData(sb.toString());
    }
}
