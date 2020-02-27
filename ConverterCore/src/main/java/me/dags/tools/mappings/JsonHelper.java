package me.dags.tools.mappings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class JsonHelper {

    public static JsonObject loadJson(File file) throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            JsonElement element = new JsonParser().parse(reader);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
            throw new IOException("invalid json: " + file);
        }
    }
}
