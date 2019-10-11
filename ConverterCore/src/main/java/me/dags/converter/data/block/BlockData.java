package me.dags.converter.data.block;

import java.util.List;

public class BlockData {

    private final int id;
    private final String name;
    private final boolean upgrade;
    private final StateData defaultState;
    private final List<StateData> states;

    public BlockData(Object name, StateData defaultState, List<StateData> states) {
        this(name, 0, defaultState, states);
    }

    public BlockData(Object name, int id, StateData defaultState, List<StateData> states) {
        this(name, id, false, defaultState, states);
    }

    public BlockData(Object name, int id, boolean upgrade, StateData defaultState, List<StateData> states) {
        this.id = id;
        this.name = "" + name;
        this.upgrade = upgrade;
        this.states = states;
        this.defaultState = defaultState;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean upgrade() {
        return upgrade;
    }

    public StateData getDefaultState() {
        return defaultState;
    }

    public List<StateData> getStates() {
        return states;
    }
}
