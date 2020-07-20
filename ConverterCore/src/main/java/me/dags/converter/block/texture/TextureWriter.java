package me.dags.converter.block.texture;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class TextureWriter implements AutoCloseable {

    private final Writer writer;
    private final JsonObject root = new JsonObject();

    private JsonObject object = null;

    public TextureWriter(Writer writer) {
        this.writer = writer;
    }

    public void startBlock(String type, String id) {
        JsonObject typeObj = root.getAsJsonObject(type);
        if (typeObj == null) {
            typeObj = new JsonObject();
            root.add(type, typeObj);
        }
        typeObj.add(id, object = new JsonObject());
    }

    public void addTexture(String side, Object texture) {
        if (object != null) {
            object.addProperty(side, getTextureString(texture));
        }
    }

    public void endBlock() {
        object = null;
    }

    @Override
    public void close() throws Exception {
        new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(sort(root), writer);
        writer.close();
    }

    private static String getTextureString(Object name) {
        String texture = name.toString();

        int i = texture.indexOf(':');
        String domain = "minecraft";
        if (i > 0) {
            domain = texture.substring(0, i);
        }

        int j = texture.lastIndexOf('/') + 1;
        String path = texture.substring(j);

        return domain + ":" + path;
    }

    private static JsonElement sort(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject result = new JsonObject();
            element.getAsJsonObject().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> result.add(e.getKey(), sort(e.getValue())));
            return result;
        }
        return element;
    }

    public static TextureWriter of(File file) throws IOException {
        return new TextureWriter(new BufferedWriter(new FileWriter(file)));
    }
}
