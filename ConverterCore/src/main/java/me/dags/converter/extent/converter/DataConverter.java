package me.dags.converter.extent.converter;

import me.dags.converter.extent.io.DataReader;
import me.dags.converter.extent.io.DataWriter;
import org.jnbt.Tag;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface DataConverter {

    boolean isOptional();

    String getInputKey();

    String getOutputKey();

    Tag<?> convert(Tag<?> tag);

    UnaryOperator<Tag<?>> IDENTITY = t -> t;

    static DataConverter create(String in, String out) {
        return create(false, in, out);
    }

    static DataConverter create(String in, String out, UnaryOperator<Tag<?>> op) {
        return create(false, in, out, op);
    }

    static DataConverter create(boolean optional, String in, String out) {
        return create(optional, in, out, IDENTITY);
    }

    static DataConverter create(String in, String out, Supplier<? extends Tag<?>> supplier) {
        return create(false, in, out, t -> t.isPresent() ? t : supplier.get());
    }

    static DataConverter create(boolean optional, String in, String out, UnaryOperator<Tag<?>> op) {
        return new DataConverter() {
            @Override
            public boolean isOptional() {
                return optional;
            }

            @Override
            public String getInputKey() {
                return in;
            }

            @Override
            public String getOutputKey() {
                return out;
            }

            @Override
            public Tag<?> convert(Tag<?> tag) {
                return op.apply(tag);
            }
        };
    }

    static void writeData(DataReader reader, DataWriter writer, List<DataConverter> converters) {
        for (DataConverter converter : converters) {
            Tag<?> input = reader.getData(converter.getInputKey());
            if (input.isAbsent() && converter.isOptional()) {
                continue;
            }
            Tag<?> output = converter.convert(input);
            writer.setData(converter.getOutputKey(), output);
        }
    }
}
