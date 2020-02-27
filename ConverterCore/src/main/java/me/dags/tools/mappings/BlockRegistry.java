package me.dags.tools.mappings;

import me.dags.converter.block.BlockState;
import me.dags.converter.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockRegistry {

    private final Map<String, StateContainer> blocks = new HashMap<>();

    public BlockRegistry(Registry<BlockState> registry) {
        for (BlockState state : registry) {
            StateContainer container = blocks.computeIfAbsent(state.getBlockName(), s -> new StateContainer());
            container.add(state);
        }
    }

    public List<BlockState> getStates(String name, int data) {
        return getStates(name, data, data);
    }

    public List<BlockState> getStates(String name, int min, int max) {
        StateContainer container = blocks.get(name);
        if (container == null) {
            new NullPointerException(name).printStackTrace();
            return Collections.emptyList();
        }

        if (min == max) {
            return container.getStates(min);
        } else {
            List<BlockState> list = new ArrayList<>();
            for (int i = min; i <= max; i++) {
                container.fill(i, list);
            }
            return list;
        }
    }
}
