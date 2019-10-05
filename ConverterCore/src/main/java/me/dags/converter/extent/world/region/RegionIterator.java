package me.dags.converter.extent.world.region;

import org.jnbt.CompoundTag;

import java.io.IOException;

public class RegionIterator extends RegionStream {

    private int x = -1;
    private int z = -1;

    public RegionIterator(MojangRegionFile region) {
        super(region);
    }

    public boolean hasNext() {
        return x < 31 || z < 31;
    }

    public CompoundTag next() throws IOException {
        if (++x > 31) {
            x = 0;
            z++;
        }
        return get(x, z);
    }
}
