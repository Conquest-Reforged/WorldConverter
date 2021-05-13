package me.dags.converter.version.versions;

import me.dags.converter.block.extender.BedExtender;
import me.dags.converter.block.extender.DoublePlantExtender_v1_12;
import me.dags.converter.block.extender.StateExtender;
import me.dags.converter.util.map.FastMap;

import java.util.Map;

public class V1_12 extends V1_10 {

    private static final Map<String, StateExtender> STATE_EXTENDERS = new FastMap<String, StateExtender>() {{
        put("minecraft:bed", new BedExtender());
        put("minecraft:double_plant", new DoublePlantExtender_v1_12());
    }};

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
