package me.dags.scraper.v1_12;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class Analyser {

    private static class StateList extends ArrayList<IBlockState> {}
    private static class PropertyList extends ArrayList<IProperty<?>> {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (IProperty p : this) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(p.getName());
            }
            return sb.toString();
        }
    }

    public static List<IBlockState> getNonTransientStates(Block block, List<IProperty<?>> props) {
        List<IBlockState> states = new LinkedList<>();
        for (IBlockState state : block.getBlockState().getValidStates()) {
            boolean allTransientsDefault = true;
            for (IProperty<?> property : props) {
                Object value = state.getValue(property);
                Object defVal = block.getDefaultState().getValue(property);
                if (!value.equals(defVal)) {
                    allTransientsDefault = false;
                    break;
                }
            }
            if (allTransientsDefault) {
                states.add(state);
            }
        }
        return states;
    }

    public static List<IBlockState> getTransientStates(Block block, List<IProperty<?>> props) {
        IBlockState base = block.getDefaultState();
        List<IBlockState> states = new LinkedList<>();
        for (IProperty<?> p1 : props) {
            for (Object v1 : p1.getAllowedValues()) {
                // ignore default values
                if (base.getValue(p1).equals(v1)) {
                    continue;
                }
                IBlockState state = with(base, p1, v1);
                for (IProperty<?> p2 : props) {
                    // ignore self
                    if (p1 == p2) {
                        continue;
                    }
                    for (Object v2 : p2.getAllowedValues()) {
                        // ignore default
                        if (base.getValue(p2).equals(v2)) {
                            continue;
                        }
                        state = with(state, p2, v2);
                    }
                }
                states.add(state);
            }
        }
        return states;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> IBlockState with(IBlockState state, IProperty<T> property, Object value) {
        return state.withProperty(property, (T) value);
    }

    public static List<IBlockState> getTransientStates(Block block) {
        StateList result = new StateList();
        StateList[] states = getAllStates(block);
        PropertyList trans = getTransientProps(states);
        for (StateList list : states) {
            if (list == null) {
                continue;
            }
            Map<List<Object>, IBlockState> unqiue = new HashMap<>();
            for (IBlockState state : list) {
                List<Object> values = new ArrayList<>();
                for (IProperty<?> p : trans) {
                    Object value = state.getValue(p);
                    values.add(value);
                }
                unqiue.put(values, state);
            }
            result.addAll(unqiue.values());
        }
        result.sort(Comparator.comparing(IBlockState::toString));
        return result;
    }

    public static List<IProperty<?>> getTransientProperties(Block block) {
        if (block.getBlockState().getValidStates().size() == 1) {
            return Collections.emptyList();
        }
        return getTransientProps(getAllStates(block));
    }

    private static StateList[] getAllStates(Block block) {
        StateList[] states = new StateList[16];
        for (IBlockState state : block.getBlockState().getValidStates()) {
            int meta = block.getMetaFromState(state);
            StateList list = states[meta];
            if (list == null) {
                states[meta] = list = new StateList();
            }
            list.add(state);
        }
        return states;
    }

    private static PropertyList getTransientProps(StateList[] states) {
        PropertyList transientProps = new PropertyList();
        for (StateList list : states) {
            if (list == null) {
                continue;
            }
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    IBlockState a = list.get(i);
                    IBlockState b = list.get(j);
                    addTransientProps(a, b, transientProps);
                }
            }
        }
        transientProps.sort(Comparator.comparing(IProperty::getName));
        return transientProps;
    }

    private static void addTransientProps(IBlockState a, IBlockState b, Collection<IProperty<?>> collection) {
        for (IProperty<?> p : a.getPropertyKeys()) {
            Object va = a.getValue(p);
            Object vb = b.getValue(p);
            if (va.equals(vb)) {
                continue;
            }
            if (collection.contains(p)) {
                continue;
            }
            collection.add(p);
        }
    }
}
