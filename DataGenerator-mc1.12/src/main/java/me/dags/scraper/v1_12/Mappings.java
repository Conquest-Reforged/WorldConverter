package me.dags.scraper.v1_12;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mappings {

    public static void generate() {
        File gameData = new File("data.json");
        if (!gameData.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(gameData)) {
            JsonElement root = new JsonParser().parse(reader);
            if (!root.isJsonObject()) {
                return;
            }
            String version = root.getAsJsonObject().get("version").getAsString();
            try (FileWriter writer = new FileWriter("blocks_" + version + "-1.14.txt")) {
                writeBlockMappings(root.getAsJsonObject().getAsJsonObject("blocks"), writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeBlockMappings(JsonObject blocks, Writer writer) {
        PrintWriter printWriter = new PrintWriter(writer);
        for (Map.Entry<String, JsonElement> block : blocks.entrySet()) {
            if (!block.getValue().isJsonObject()) {
                continue;
            }

            List<NBTTagCompound> states = parseStates(block.getKey(), block.getValue().getAsJsonObject());
            for (NBTTagCompound state : states) {
                String from = serialize(state);
                String to = serialize(state);

                printWriter.print(from);
                printWriter.print(" -> ");
                printWriter.print(to);
                printWriter.println();
            }
        }
        printWriter.flush();
    }

    private static List<NBTTagCompound> parseStates(String name, JsonObject block) {
        JsonElement def = block.get("default");
        JsonElement variants = block.get("states");
        if (def == null || variants == null) {
            return Collections.singletonList(parse(name, ""));
        }

        NBTTagCompound defaults = parse(name, def.getAsString());
        if (variants.isJsonArray()) {
            List<NBTTagCompound> states = new LinkedList<>();
            for (JsonElement state : variants.getAsJsonArray()) {
                states.add(parse(name, state.getAsString()));
            }
            return states;
        } else if (variants.isJsonObject()) {
            NBTTagCompound[] states = new NBTTagCompound[16];
            for (Map.Entry<String, JsonElement> e : variants.getAsJsonObject().entrySet()) {
                int meta = e.getValue().getAsInt();
                NBTTagCompound state = parse(name, e.getKey());
                NBTTagCompound current = states[meta];
                if (current == null) {
                    states[meta] = state;
                } else if (compare(state, current, defaults) < 0){
                    states[meta] = state;
                }
            }
            List<NBTTagCompound> list = new LinkedList<>();
            for (NBTTagCompound state : states) {
                if (state != null) {
                    list.add(state);
                }
            }
            return list;
        } else {
            return Collections.singletonList(defaults);
        }
    }

    private static NBTTagCompound parse(String name, String properties) {
        NBTTagCompound root = new NBTTagCompound();
        root.setString("Name", name);
        if (properties.isEmpty()) {
            return root;
        }

        try {
            NBTTagCompound props = new NBTTagCompound();
            String[] pairs = properties.split(",");
            for (String pair : pairs) {
                String[] keyVal = pair.split("=");
                props.setString(keyVal[0], keyVal[1]);
            }
            root.setTag("Properties", props);
            return root;
        } catch (Throwable t) {
            System.out.println("##### " + name + " : " + properties);
            throw new RuntimeException(t);
        }
    }

    private static String serialize(NBTTagCompound state) {
        String name = state.getString("Name");
        if (state.hasKey("Properties")) {
            NBTTagCompound props = state.getCompoundTag("Properties");
            StringBuilder sb = new StringBuilder(name);
            sb.append('[');
            serializeProperties(sb, props);
            sb.append(']');
            return sb.toString();
        }
        return name;
    }

    private static String serializeProperties(NBTTagCompound props) {
        return serializeProperties(new StringBuilder(), props).toString();
    }

    private static StringBuilder serializeProperties(StringBuilder sb, NBTTagCompound props) {
        boolean first = true;
        for (String key : props.getKeySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(key).append('=').append(props.getString(key));
        }
        return sb;
    }

    private static int compare(NBTTagCompound a, NBTTagCompound b, NBTTagCompound defaults) {
        a = a.getCompoundTag("Properties");
        b = b.getCompoundTag("Properties");
        defaults = defaults.getCompoundTag("Properties");

        int score = 0;
        for (String key : a.getKeySet()) {
            Object aVal = a.getString(key);
            Object bVal = b.getString(key);
            if (aVal.equals(bVal)) {
                continue;
            }
            Object defVal = defaults.getString(key);
            if (aVal.equals(defVal)) {
                score--;
            } else if (bVal.equals(defVal)) {
                score++;
            }
        }
        return score;
    }
}
