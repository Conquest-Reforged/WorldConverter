package me.dags.scraper.v1_12;

import com.google.gson.stream.JsonWriter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Connections {

    private static final Map<String, String> ALL = new HashMap<>();

    private interface Visitor {

        void visit(Block block) throws Throwable;
    }

    private static void iterate(Visitor visitor) throws Throwable {
        Set<Class<?>> visited = new HashSet<>(200);
        for (Block block : ForgeRegistries.BLOCKS) {
            if (visited.add(block.getClass())) {
                visitor.visit(block);
            }
        }
    }

    public static void visitConnections() throws Throwable {
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter("connections.json")))) {
            writer.setIndent("  ");
            writer.beginObject();
            iterate(block -> {
                if (block.getDefaultState().isFullBlock() || block.getDefaultState().isFullCube()) {
                    return;
                }

                List<IProperty<?>> props = Analyser.getTransientProperties(block);
                if (props.isEmpty()) {
                    return;
                }

                visitBlock(writer, block, props);
            });
            writer.endObject();
        }
    }

    private static void visitBlock(JsonWriter writer, Block block, List<IProperty<?>> props) throws Throwable {
        Map<EnumFacing, Map<String, String>> connections = new LinkedHashMap<>();

        iterate(other -> {
            Map<EnumFacing, Map<String, String>> map = getConnections(block, other, props);
            for (Map.Entry<EnumFacing, Map<String, String>> e : map.entrySet()) {
                connections.computeIfAbsent(e.getKey(), f -> new LinkedHashMap<>()).putAll(e.getValue());
            }
        });


        boolean empty = true;
        for (Map m : connections.values()) {
            if (!m.isEmpty()) {
                empty = false;
                break;
            }
        }
        if (empty) {
            return;
        }

        writer.name(block.getClass().getName()).beginObject();
        write(writer, connections);
        writer.endObject();
    }

    private static Map<EnumFacing, Map<String, String>> getConnections(Block from, Block to, List<IProperty<?>> props) {
        Map<EnumFacing, Map<String, String>> connections = new LinkedHashMap<>();
        List<IBlockState> states = Analyser.getNonTransientStates(from, props);
        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            Map<String, String> map = getConnections(from, to, direction, props);
            connections.put(direction, map);
        }
        return connections;
    }

    private static Map<String, String> getConnections(Block from, Block to, EnumFacing direction, List<IProperty<?>> props) {
        String baseName = to.getClass().getName();
        Map<String, String> connections = new LinkedHashMap<>();
        for (IBlockState state : Analyser.getNonTransientStates(from, props)) {
            for (IBlockState neighbour : to.getBlockState().getValidStates()) {
                NeighbourReader reader = new NeighbourReader(state, neighbour, direction);
                IBlockState result = reader.getActualState();
                if (result.equals(state) || result.equals(state.getBlock().getDefaultState())) {
                    continue;
                }

                String key = baseName + "[" + propertyString(neighbour) + "]";
                String value = propertyDifString(result);
                connections.put(key, value);
            }
        }
        if (connections.size() == to.getBlockState().getValidStates().size()) {
            String common = null;
            for (String value : connections.values()) {
                if (common == null) {
                    common = value;
                } else if (!common.equals(value)) {
                    return connections;
                }
            }
            if (common != null) {
                return Collections.singletonMap(baseName, common);
            }
        }
        return connections;
    }

    private static String propertyString(IBlockState state) {
        StringBuilder sb = new StringBuilder();
        state.getPropertyKeys().stream().sorted(Comparator.comparing(IProperty::getName)).forEach(p -> {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p.getName()).append('=').append(state.getValue(p).toString().toLowerCase());
        });
        return sb.toString();
    }

    private static String nonTransientProps(IBlockState state, List<IProperty<?>> trans) {
        StringBuilder sb = new StringBuilder();
        state.getPropertyKeys().stream().filter(p -> !trans.contains(p)).sorted(Comparator.comparing(IProperty::getName)).forEach(p -> {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p.getName()).append('=').append(state.getValue(p));
        });
        return sb.toString();
    }

    private static String transientProps(IBlockState state, List<IProperty<?>> trans) {
        StringBuilder sb = new StringBuilder();
        state.getPropertyKeys().stream().filter(trans::contains).sorted(Comparator.comparing(IProperty::getName)).forEach(p -> {
            Object value = state.getValue(p).toString().toLowerCase();
            Object defValue = state.getBlock().getDefaultState().getValue(p).toString().toLowerCase();
            if (value.equals(defValue)) {
                return;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p.getName()).append('=').append(value);
        });
        return sb.toString();
    }

    private static String propertyDifString(IBlockState state) {
        StringBuilder sb = new StringBuilder();
        state.getPropertyKeys().stream().sorted(Comparator.comparing(IProperty::getName)).forEach(p -> {
            Object value = state.getValue(p).toString().toLowerCase();
            Object defValue = state.getBlock().getDefaultState().getValue(p).toString().toLowerCase();
            if (value.equals(defValue)) {
                return;
            }

            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p.getName()).append('=').append(value);
        });
        return sb.toString();
    }

    private static void write(JsonWriter writer, Map<?, ?> map) throws IOException {
        if (map.isEmpty()) {
            return;
        }

        for (Map.Entry<?, ?> e : map.entrySet()) {
            Object key = e.getKey();
            Object value = e.getValue();

            if (value instanceof Map) {
                Map child = (Map) value;
                if (!child.isEmpty()) {
                    writer.name(key.toString()).beginObject();
                    write(writer, child);
                    writer.endObject();
                }
            } else {
                writer.name(key.toString());
                writer.value(value.toString());
            }
        }
    }
}
