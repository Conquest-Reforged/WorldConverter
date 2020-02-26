package me.dags.converter.version.versions;

import me.dags.converter.version.format.BiomeFormat;

public class V1_15 extends V1_14 {

    @Override
    public int getId() {
        return 2227;
    }

    @Override
    public String getVersion() {
        return "1.15";
    }

    @Override
    public BiomeFormat getBiomeFormat() {
        return BiomeFormat.LATEST;
    }
}
