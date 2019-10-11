package me.dags.converter.converter.world.region;

import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegionStream implements AutoCloseable {

    private static final CompoundTag empty = Nbt.compound().getCompound("null");

    private final MojangRegionFile region;

    public RegionStream(MojangRegionFile region) {
        this.region = region;
    }

    public CompoundTag get(int x, int z) throws IOException {
        try (DataInputStream in = region.getChunkDataInputStream(x, z)) {
            if (in == null) {
                return empty;
            }
            return Nbt.read(in).getTag().asCompound();
        }
    }

    public void set(int x, int z, CompoundTag chunk) throws IOException {
        try (DataOutputStream out = region.getChunkDataOutputStream(x, z)) {
            Nbt.write(chunk, (DataOutput) out);
        }
    }

    @Override
    public void close() throws Exception {
        region.close();
    }
}
