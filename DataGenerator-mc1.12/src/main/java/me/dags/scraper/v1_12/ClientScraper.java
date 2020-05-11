package me.dags.scraper.v1_12;

import me.dags.converter.block.texture.BlockDumper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClientScraper {

    public static void dump() {
        new BlockDumper<IBlockState>() {
            @Override
            public String getBlockClass(IBlockState o) {
                return o.getBlock().getClass().getSimpleName();
            }

            @Override
            public void eachState(Consumer<IBlockState> consumer) {
                for (Block block : ForgeRegistries.BLOCKS) {
                    if (!(block.getRegistryName() + "").startsWith("minecraft")) {
                        for (IBlockState state : block.getBlockState().getValidStates()) {
                            consumer.accept(state);
                        }
                    }
                }
            }

            @Override
            public void eachTexture(IBlockState state, BiConsumer<String, Object> consumer) {
                IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
                iterateTextures(state, model, consumer);
            }
        }.run("1.12");
    }

    private static void iterateTextures(IBlockState state, IBakedModel model, BiConsumer<String, Object> consumer) {
        String particle = model.getParticleTexture().getIconName();

        if (!isMissing(particle)) {
            consumer.accept("particle", particle);
        }

        List<BakedQuad> quads = model.getQuads(state, null, 0L);
        for (BakedQuad quad : quads) {
            consumer.accept(quad.getFace().getName(), quad.getSprite().getIconName());
        }

        for (EnumFacing side : EnumFacing.values()) {
            quads = model.getQuads(state, side, 0L);
            if (quads == null || quads.isEmpty()) {
                continue;
            }

            for (BakedQuad quad : quads) {
                consumer.accept(quad.getFace().getName(), quad.getSprite().getIconName());
            }
        }
    }

    private static boolean isMissing(String texture) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes()
                .getModelManager().getMissingModel().getParticleTexture().getIconName()
                .equals(texture);
    }
}
