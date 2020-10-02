package me.dags.tools.mappings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.converter.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Mappings {

    private final Map<Mapping, Mapping> mappings = Utils.newOrderedMap();

    public Mappings(File file) throws IOException {
        JsonObject root = JsonHelper.loadJson(file);
        JsonArray blocks = root.getAsJsonArray("blocks");
        for (JsonElement e : blocks) {
            if (e.isJsonObject()) {
                readOne(e.getAsJsonObject());
            }
        }
    }

    public int getSize() {
        return mappings.size();
    }

    public void forEach(Visitor visitor) throws IOException {
        for (Map.Entry<Mapping, Mapping> e : mappings.entrySet()) {
            visitor.visit(e.getKey(), e.getValue());
        }
    }

    public interface Visitor {

        void visit(Mapping from, Mapping to) throws IOException;
    }

    private void readOne(JsonObject entry) {
        String fromName = entry.get("name").getAsString();
        int fromDataMin = entry.get("min").getAsInt();
        int fromDataMax = entry.get("max").getAsInt();
        Mapping in = new Mapping(fromName, fromDataMin, fromDataMax);

        JsonObject to = entry.getAsJsonObject("to");
        String toName = to.get("name").getAsString();
        int toData = to.get("data").getAsInt();
        Mapping out = new Mapping(toName, toData, toData);

        mappings.put(in, out);
    }

    public static class Mapping {

        public final String blockName;
        public final int minData;
        public final int maxData;

        private Mapping(String blockName, int minData, int maxData) {
            this.blockName = blockName;
            this.minData = minData;
            this.maxData = maxData;
        }
    }
}
