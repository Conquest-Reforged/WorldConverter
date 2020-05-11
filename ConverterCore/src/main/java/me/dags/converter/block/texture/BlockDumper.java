package me.dags.converter.block.texture;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface BlockDumper<BlockState> {

    void eachState(Consumer<BlockState> consumer);

    void eachTexture(BlockState state, BiConsumer<String, Object> consumer);

    String getBlockClass(BlockState state);

    default String getStateString(BlockState state) {
        return state.toString();
    }

    default void run(String version) {
        try (TextureWriter writer = TextureWriter.of(new File("blocks-" + version + ".json"))) {
            eachState(state -> {
                writer.startBlock(getBlockClass(state), getStateString(state));
                eachTexture(state, writer::addTexture);
                writer.endBlock();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
