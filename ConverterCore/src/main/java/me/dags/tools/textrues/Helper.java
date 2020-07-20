package me.dags.tools.textrues;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Helper {

    private static final Map<String, String> typeMappings = new LinkedHashMap<>();
    private static final Map<String, String> textureMappings = new LinkedHashMap<>();

    static {
        parse("block_types.txt", typeMappings);
        parse("textures.txt", textureMappings);

        typeMappings.put("BlockStairsMeta", "Stairs");
    }

    public static String getType(String name) {
        return typeMappings.getOrDefault(name, name);
    }

    public static String getTexture(String texture) {
        return textureMappings.getOrDefault(texture, texture);
    }

    public static JsonObject upgrade(JsonObject in) {
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> e : in.entrySet()) {
            JsonObject setOut = new JsonObject();
            out.add(getType(e.getKey()), setOut);

            JsonObject setIn = e.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> block : setIn.entrySet()) {
                JsonObject texturesOut = new JsonObject();
                setOut.add(block.getKey(), texturesOut);

                JsonObject texturesIn = block.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> texture : texturesIn.entrySet()) {
                    texturesOut.addProperty(texture.getKey(), getTexture(texture.getValue().getAsString()));
                }
            }
        }
        return out;
    }

    private static void parse(String name, Map<String, String> map) {
        try (Scanner reader = new Scanner(name)) {
            String line = reader.nextLine();
            String[] parts = line.split(" -> ");
            if (parts.length == 2) {
                map.put(parts[0], parts[1]);
            }
        }
    }
}
