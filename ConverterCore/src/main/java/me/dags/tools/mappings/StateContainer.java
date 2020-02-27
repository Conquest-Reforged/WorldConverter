package me.dags.tools.mappings;

import me.dags.converter.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class StateContainer {

    private final List<BlockState> states = new ArrayList<>();

    public void add(BlockState state) {
        states.add(state);
    }

    public void fill(int metadata, List<BlockState> list) {
        for (BlockState state : states) {
            if (BlockState.getMetaData(state.getId()) == metadata) {
                list.add(state);
            }
        }
    }

    public List<BlockState> getStates(int metadata) {
        List<BlockState> list = new ArrayList<>();
        fill(metadata, list);
        return list;
    }
}
