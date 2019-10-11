package me.dags.converter.extent.chunk;

import java.util.LinkedList;
import java.util.List;

public class UpgradeData {

    private final List<Integer> indices = new LinkedList<>();

    public void mark(int dx, int dy, int dz) {
        mark((dy << 8) + (dz << 4) + dx);
    }

    public void mark(int index) {
        indices.add(index);
    }

    public int[] toArray() {
        int[] array = new int[indices.size()];
        int index = 0;
        for (int i : indices) {
            array[index] = i;
            index++;
        }
        return array;
    }
}
