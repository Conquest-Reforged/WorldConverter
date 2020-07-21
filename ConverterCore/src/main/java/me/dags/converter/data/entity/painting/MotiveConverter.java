package me.dags.converter.data.entity.painting;

import me.dags.converter.converter.DataConverter;
import me.dags.converter.util.Utils;
import org.jnbt.Nbt;
import org.jnbt.Tag;

public class MotiveConverter implements DataConverter {

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public String getInputKey() {
        return "Motive";
    }

    @Override
    public String getOutputKey() {
        return "Motive";
    }

    @Override
    public Tag<?> convert(Tag<?> tag) {
        String motive = tag.asString().getValue();
        String replacement = convert(motive);
        return Nbt.stringTag(replacement);
    }

    private static String convert(String motive) {
        if (motive.contains(":")) {
            return motive;
        }
        return Utils.toIdentifier("minecraft", motive);
    }
}
