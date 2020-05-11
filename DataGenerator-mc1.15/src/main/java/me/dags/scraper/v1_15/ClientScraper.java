package me.dags.scraper.v1_15;

import me.dags.converter.block.texture.BlockDumper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientScraper {

    private static boolean first = true;

    @SubscribeEvent
    public static void dump(TickEvent.RenderTickEvent event) {
        if (!first) {
            return;
        }

        first = false;

        new BlockDumper<BlockState>() {
            @Override
            public void eachState(Consumer<BlockState> consumer) {
                for (Block block : ForgeRegistries.BLOCKS) {
                    if (!(block.getRegistryName() + "").startsWith("minecraft")) {
                        for (BlockState state : block.getStateContainer().getValidStates()) {
                            consumer.accept(state);
                        }
                    }
                }
            }

            @Override
            public void eachTexture(BlockState state, BiConsumer<String, Object> consumer) {
                IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
                iterateTextures(state, model, consumer);
            }

            @Override
            public String getStateString(BlockState state) {
                return ClientScraper.getStateString(state);
            }

            @Override
            public String getBlockClass(BlockState state) {
                return state.getBlock().getClass().getSimpleName();
            }
        }.run("1.15");
    }

    private static void iterateTextures(BlockState state, IBakedModel model, BiConsumer<String, Object> consumer) {
        Random random = new Random(0L);

        ResourceLocation particle = model.getParticleTexture(null).getName();
        if (!isMissing(particle)) {
            consumer.accept("particle", particle);
        }

        List<BakedQuad> quads = model.getQuads(state, null, random, null);
        for (BakedQuad quad : quads) {
            consumer.accept(quad.getFace().getName(), quad.func_187508_a().getName());
        }

        for (Direction side : Direction.values()) {
            quads = model.getQuads(state, side, random, null);
            if (quads == null || quads.isEmpty()) {
                continue;
            }

            for (BakedQuad quad : quads) {
                consumer.accept(quad.getFace().getName(), quad.func_187508_a().getName());
            }
        }
    }

    private static String getStateString(BlockState state) {
        if (state.getProperties().isEmpty()) {
            return "" + state.getBlock().getRegistryName();
        } else {
            StringBuilder sb = new StringBuilder().append(state.getBlock().getRegistryName()).append('[');
            int len = sb.length();
            state.getProperties().stream().sorted(Comparator.comparing(IProperty::getName)).forEach(p -> {
                if (sb.length() > len) {
                    sb.append(',');
                }
                sb.append(p.getName()).append('=').append(state.get(p).toString().toLowerCase());
            });
            sb.append(']');
            return sb.toString();
        }
    }

    private static boolean isMissing(ResourceLocation name) {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes()
                .getModelManager().getMissingModel().getParticleTexture().getName()
                .equals(name);
    }
}
