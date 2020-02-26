package thing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Mappings {

    public static void main(String[] args) {

    }

    private static void generate(File levelFrom, File levelTo, File mappings) throws IOException {
        Registry from = new Registry(levelFrom);
        Registry to = new Registry(levelTo);
        try (Reader reader = new BufferedReader(new FileReader(mappings))) {
            JsonElement el = new JsonParser().parse(reader);
            if (!el.isJsonObject()) {
                throw new RuntimeException("Invalid mappings json");
            }

            JsonArray array = el.getAsJsonObject().getAsJsonArray("blocks");
            if (array == null) {
                throw new RuntimeException("Invalid mappings json");
            }

            for (JsonElement e : array) {

            }
        }
    }
}
