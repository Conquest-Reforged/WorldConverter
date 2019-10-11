package me.dags.converter.registry;

import me.dags.converter.util.storage.IntMap;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Scanner;

public class Mapper<T extends RegistryItem> implements Registry.Mapper<T> {

    private final String version;
    private final IntMap<T> mappings;

    private Mapper(String version, IntMap<T> mappings) {
        this.version = version;
        this.mappings = mappings;
    }

    @Override
    public T apply(T in) {
        return mappings.getOrDefault(in.getId(), in);
    }

    @Override
    public String getVersion() {
        return version;
    }

    public static <T extends RegistryItem> Builder<T> builder(Registry<T> from, Registry<T> to) {
        return new Builder<>(from, to);
    }

    public static class Builder<T extends RegistryItem> {

        private final Registry<T> from;
        private final Registry<T> to;
        private final IntMap<T> mappings = new IntMap<>(4096);

        private Builder(Registry<T> from, Registry<T> to) {
            this.from = from;
            this.to = to;
        }

        public boolean hasMappings() {
            return mappings.size() > 0;
        }

        public Builder<T> parse(InputStream inputStream) throws ParseException {
            try (Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNext()) {
                    parse(scanner.nextLine());
                }
            }
            return this;
        }

        public Builder<T> parse(String rule) throws ParseException {
            if (rule.startsWith("#")) {
                return this;
            }

            String[] args = rule.split("->");
            if (args.length != 2) {
                return this;
            }

            String in = args[0].trim();
            String out = args[1].trim();
            return parse(in, out);
        }

        public Builder<T> parse(String in, String out) throws ParseException {
            T from = this.from.getParser().parse(in);
            T to = this.to.getParser().parse(out);
            mappings.put(this.from.getId(from), to);
            return this;
        }

        public Mapper<T> build() {
            String version = from.getVersion() + "-" + to.getVersion();
            return new Mapper<>(version, mappings.copy());
        }
    }
}
