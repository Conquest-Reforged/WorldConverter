package me.dags.tools.textrues;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        try (FileWriter writer = new FileWriter("generated-mappings.txt")) {
            JsonObject a = Helper.upgrade(Main.open("blocks-1.12"));
            JsonObject b = Main.open("blocks-1.15");

            for (Map.Entry<String, JsonElement> e : a.entrySet()) {
                JsonObject typeA = e.getValue().getAsJsonObject();
                JsonObject typeB = b.getAsJsonObject(e.getKey());
                if (typeB == null) {
                    continue;
                }

                for (Map.Entry<String, JsonElement> blockA : typeA.entrySet()) {
                    JsonObject modelA = blockA.getValue().getAsJsonObject();

                    for (Map.Entry<String, JsonElement> blockB : typeB.entrySet()) {
                        JsonObject modelB = blockB.getValue().getAsJsonObject();

                        if (countMatches(modelA, modelB) == modelA.size()) {
                            writer.write(blockA.getKey() + " -> " + blockB.getKey() + "\n");
                        }
                    }
                }
            }
        }
    }

    private static int countMatches(JsonObject modelA, JsonObject modelB) {
        int count = 0;
        for (Map.Entry<String, JsonElement> textureA : modelA.entrySet()) {
            if (modelB.has(textureA.getKey())) {
                JsonElement textureB = modelB.get(textureA.getKey());
                if (textureA.getValue().equals(textureB)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static JsonObject open(String name) throws IOException {
        try (Reader reader = new FileReader(name + ".json")) {
            return new JsonParser().parse(reader).getAsJsonObject();
        }
    }
}
