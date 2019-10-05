package me.dags.converter.data.block;

public class StateData {

    private final String name;
    private final int id;

    public StateData(String name) {
        this(name, 0);
    }

    public StateData(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
