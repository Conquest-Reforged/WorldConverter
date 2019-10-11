package me.dags.converter.extent.world.region;

import me.dags.converter.extent.converter.Converter;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import org.jnbt.CompoundTag;

import java.io.File;
import java.util.concurrent.Callable;

public class RegionTask implements Callable<Void> {

    private final File in;
    private final File out;
    private final Converter converter;

    public RegionTask(File input, File output, Converter converter) {
        this.in = input;
        this.out = output;
        this.converter = converter;
    }

    @Override
    public Void call() throws Exception {
        try (RegionStream input = new RegionStream(new MojangRegionFile(in))) {
            IO.makeFile(out);
            try (RegionStream output = new RegionStream(new MojangRegionFile(out))) {
                for (int z = 0; z < 32; z++) {
                    for (int x = 0; x < 32; x++) {
                        CompoundTag in = input.get(x, z);
                        if (in.isPresent()) {
                            CompoundTag out = converter.convert(in);
                            output.set(x, z, out);
                        }
                    }
                }
            }
        }
        Logger.log("Converted region:", in.getName());
        return null;
    }
}
