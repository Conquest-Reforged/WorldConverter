package me.dags.converter.block.extender;

import org.jnbt.Tag;

public class BedExtender extends SimpleTileStateExtender {

    private static final String[] COLORS = {
            "white",        // 00
            "orange",       // 01
            "magenta",      // 02
            "light_blue",   // 03
            "yellow",       // 04
            "lime",         // 05
            "pink",         // 06
            "gray",         // 07
            "light_gray",   // 08
            "cyan",         // 09
            "purple",       // 10
            "blue",         // 11
            "brown",        // 12
            "green",        // 13
            "red",          // 14
            "black",        // 15
    };

    public BedExtender() {
        super("minecraft:bed", "color");
    }

    @Override
    protected String toString(String name, Tag<?> tag) {
        Object value = tag.getValue();
        int id = value instanceof Integer ? (Integer) value : 0;
        if (id < 0 || id >= BedExtender.COLORS.length) {
            id = 0;
        }
        return BedExtender.COLORS[id];
    }
}
