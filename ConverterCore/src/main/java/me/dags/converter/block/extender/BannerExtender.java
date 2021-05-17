package me.dags.converter.block.extender;

import org.jnbt.Tag;

public class BannerExtender extends SimpleTileStateExtender {

    private static final String[] COLORS = {
            "black",        // 00
            "red",          // 01
            "green",        // 02
            "brown",        // 03
            "blue",         // 04
            "purple",       // 05
            "cyan",         // 06
            "light_gray",   // 07
            "gray",         // 08
            "pink",         // 09
            "lime",         // 10
            "yellow",       // 11
            "light_blue",   // 12
            "magenta",      // 13
            "orange",       // 14
            "white",        // 15
    };

    public BannerExtender() {
        super("minecraft:banner", "Base");
    }

    @Override
    protected String toString(String name, Tag<?> tag) {
        Object value = tag.getValue();
        int id = value instanceof Integer ? (Integer) value : 0;
        if (id < 0 || id >= BannerExtender.COLORS.length) {
            id = 0;
        }
        return BannerExtender.COLORS[id];
    }
}
