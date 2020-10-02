package me.dags.converter.datagen.mappings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.converter.block.Serializer;
import me.dags.converter.util.Utils;
import me.dags.converter.util.log.Logger;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StateRegistry {

    private static final CompoundTag empty = Nbt.compound().getCompound("empty");

    private final Function<String, CompoundTag> defaults;
    private final Function<String, List<CompoundTag>> states;
    private final Map<String, CompoundTag> defaultsCache = Utils.newMap();
    private final Map<String, List<CompoundTag>> statesCache = Utils.newMap();

    public StateRegistry(Computer<CompoundTag> defaults, Computer<List<CompoundTag>> states) {
        this.states = name -> {
            try {
                return states.compute(name);
            } catch (Throwable e) {
                Logger.log(e);
                return Collections.emptyList();
            }
        };

        this.defaults = name -> {
            try {
                return defaults.compute(name);
            } catch (Throwable e) {
                Logger.log(e);
                return empty;
            }
        };
    }

    public CompoundTag getDefault(String name) {
        return defaultsCache.computeIfAbsent(name, defaults);
    }

    public List<CompoundTag> getStates(String blockName) {
        return statesCache.computeIfAbsent(blockName, states);
    }

    interface Computer<T> {

        T compute(String key) throws ParseException;
    }

    private static Computer<CompoundTag> defaults(JsonObject blocks) {
        return name -> {
            JsonObject block = blocks.getAsJsonObject(name);
            JsonElement defaults = block.get("default");
            if (defaults == null) {
                return Serializer.deserialize(name);
            }
            return Serializer.deserialize(name, defaults.getAsString());
        };
    }

    public static StateRegistry create(JsonObject blocks) {
        return new StateRegistry(defaults(blocks), name -> {
            JsonObject block = blocks.getAsJsonObject(name);
            JsonArray states = block.getAsJsonArray("states");
            if (states == null || states.size() == 1) {
                return Collections.singletonList(Nbt.compound(1).put("Name", name));
            } else {
                List<CompoundTag> list = new LinkedList<>();
                for (JsonElement element : states) {
                    CompoundTag state = Serializer.deserialize(name, element.getAsString());
                    list.add(state);
                }
                return list;
            }
        });
    }

    public static StateRegistry createLegacy(JsonObject blocks) {
        return new StateRegistry(defaults(blocks), name -> {
            JsonObject block = blocks.getAsJsonObject(name);
            JsonObject states = block.getAsJsonObject("states");
            if (states == null || states.size() == 1) {
                return Collections.singletonList(Nbt.compound(1).put("Name", name));
            } else {
                List<CompoundTag> list = new LinkedList<>();
                for (Map.Entry<String, ?> entry : states.entrySet()) {
                    CompoundTag state = Serializer.deserialize(name, entry.getKey());
                    list.add(state);
                }
                return list;
            }
        });
    }
}
