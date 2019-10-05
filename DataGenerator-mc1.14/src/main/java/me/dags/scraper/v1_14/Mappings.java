package me.dags.scraper.v1_14;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dags.converter.resource.Container;
import me.dags.converter.resource.Resource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.io.*;
import java.nio.file.Paths;
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
            try (FileWriter writer = new FileWriter("biomes_" + version + "-1.14.txt")) {
                writeBiomeMappings(root.getAsJsonObject().getAsJsonObject("biomes"), writer);
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

            List<CompoundNBT> states = parseStates(block.getKey(), block.getValue().getAsJsonObject());
            for (CompoundNBT state : states) {
                if (!DataFixers.fixable(state)) {
                    continue;
                }

                String from = serialize(state);

                CompoundNBT output = DataFixers.fixState(state);
                String to = serialize(output);

                printWriter.print(from);
                printWriter.print(" -> ");
                printWriter.print(to);
                printWriter.println();
            }
        }
        printWriter.flush();
    }

    private static void writeBiomeMappings(JsonObject biomes, Writer writer) {
        PrintWriter printWriter = new PrintWriter(writer);
        for (Map.Entry<String, JsonElement> entry : biomes.entrySet()) {
            String from = entry.getKey();
            String to = DataFixers.fixBiome(from);
            if (from.equals(to)) {
                continue;
            }
            printWriter.print(from);
            printWriter.print(" -> ");
            printWriter.print(to);
            printWriter.println();
        }
    }

    private static List<CompoundNBT> parseStates(String name, JsonObject block) {
        JsonElement def = block.get("default");
        JsonElement variants = block.getAsJsonObject("states");
        if (def == null || variants == null) {
            return Collections.singletonList(parse(name, ""));
        }

        CompoundNBT defaults = parse(name, def.getAsString());
        if (variants.isJsonArray()) {
            List<CompoundNBT> states = new LinkedList<>();
            for (JsonElement state : variants.getAsJsonArray()) {
                states.add(parse(name, state.getAsString()));
            }
            return states;
        } else if (variants.isJsonObject()) {
            CompoundNBT[] states = new CompoundNBT[16];
            for (Map.Entry<String, JsonElement> e : variants.getAsJsonObject().entrySet()) {
                int meta = e.getValue().getAsInt();
                CompoundNBT state = parse(name, e.getKey());
                CompoundNBT current = states[meta];
                if (current == null) {
                    states[meta] = state;
                } else if (compare(state, current, defaults) < 0){
                    states[meta] = state;
                }
            }
            List<CompoundNBT> list = new LinkedList<>();
            for (CompoundNBT state : states) {
                if (state != null) {
                    list.add(state);
                }
            }
            return list;
        } else {
            return Collections.singletonList(defaults);
        }
    }

    private static CompoundNBT parse(String name, String properties) {
        CompoundNBT root = new CompoundNBT();
        root.putString("Name", name);
        if (properties.isEmpty()) {
            return root;
        }

        try {
            CompoundNBT props = new CompoundNBT();
            String[] pairs = properties.split(",");
            for (String pair : pairs) {
                String[] keyVal = pair.split("=");
                props.putString(keyVal[0], keyVal[1]);
            }
            root.put("Properties", props);
            return root;
        } catch (Throwable t) {
            System.out.println("##### " + name + " : " + properties);
            throw new RuntimeException(t);
        }
    }

    private static String serialize(CompoundNBT state) {
        String name = state.getString("Name");
        if (state.contains("Properties")) {
            CompoundNBT props = state.getCompound("Properties");
            StringBuilder sb = new StringBuilder(name);
            sb.append('[');
            serializeProperties(sb, props);
            sb.append(']');
            return sb.toString();
        }
        return name;
    }

    private static String serializeProperties(CompoundNBT props) {
        return serializeProperties(new StringBuilder(), props).toString();
    }

    private static StringBuilder serializeProperties(StringBuilder sb, CompoundNBT props) {
        boolean first = true;
        for (String key : props.keySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(key).append('=').append(props.getString(key));
        }
        return sb;
    }

    private static int compare(CompoundNBT a, CompoundNBT b, CompoundNBT defaults) {
        a = a.getCompound("Properties");
        b = b.getCompound("Properties");
        defaults = defaults.getCompound("Properties");

        int score = 0;
        for (String key : a.keySet()) {
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
