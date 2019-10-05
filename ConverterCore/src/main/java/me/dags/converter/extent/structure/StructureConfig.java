package me.dags.converter.extent.structure;

import com.google.gson.JsonObject;
import me.dags.converter.block.BlockState;

import java.util.function.Predicate;

public class StructureConfig {

    public boolean relativize = true;
    public Predicate<BlockState> filter = BlockState::isAir;
    public Predicate<String> originBlocks = s -> true;

    public static StructureConfig parse(JsonObject config) {
        String[] skip = {"grass", "fern", "bush"};
        StructureConfig structureConfig = new StructureConfig();
        structureConfig.filter = b -> b.isAir() || contains(b, skip);
        structureConfig.originBlocks = s -> s.contains("log");
        return structureConfig;
    }

    private static boolean contains(BlockState state, String[] names) {
        for (String name : names) {
            if (state.getIdentifier().contains(name)) {
                return true;
            }
        }
        return false;
    }
}
