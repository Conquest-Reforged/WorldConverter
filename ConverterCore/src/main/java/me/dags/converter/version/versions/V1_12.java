package me.dags.converter.version.versions;

import me.dags.converter.block.extender.BedExtender;
import me.dags.converter.block.extender.StateExtender;
import me.dags.converter.util.Utils;

import java.util.Map;

public class V1_12 extends V1_10 {

    private static final Map<String, StateExtender> STATE_EXTENDERS = Utils.mapOf("minecraft:bed", new BedExtender());

    @Override
    public int getId() {
        return 1343;
    }

    @Override
    public String getVersion() {
        return "1.12";
    }

    @Override
    protected Map<String, StateExtender> getExtenders() {
        return STATE_EXTENDERS;
    }
}
