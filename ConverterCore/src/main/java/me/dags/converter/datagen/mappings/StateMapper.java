package me.dags.converter.datagen.mappings;

import me.dags.converter.block.Serializer;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StateMapper {

    private final StateRegistry from;
    private final StateRegistry to;

    public StateMapper(StateRegistry from, StateRegistry to) {
        this.from = from;
        this.to = to;
    }

    public void map(Rule in, Rule out, Consumer<String> consumer) {
        List<CompoundTag> statesIn = from.getStates(in.getName());
        if (statesIn.isEmpty()) {
            return;
        }

        for (CompoundTag stateIn : statesIn) {
            if (in.matches(stateIn)) {
                Rule rule = in.apply(out, stateIn);
                rule.fill(to.getDefault(rule.getName()));
                List<CompoundTag> statesOut = to.getStates(rule.getName());
                for (CompoundTag stateOut : statesOut) {
                    if (rule.matches(stateOut)) {
                        String left = Serializer.serialize(stateIn);
                        String right = Serializer.serialize(stateOut);
                        consumer.accept(left + " -> " + right);
                        break;
                    }
                }
            }
        }
//
//        CompoundTag matchProps = in.getCompound("Properties");
//        CompoundTag propsOut = out.getCompound("Properties");
//
//        outer:
//        for (CompoundTag state : statesIn) {
//            String name = out.getString("Name");
//            CompoundTag propsIn = state.getCompound("Properties");
//            for (Map.Entry<String, Tag> e : matchProps.getBacking().entrySet()) {
//                String key = e.getKey();
//                String value = e.getValue().asString().getValue();
//                Tag tagIn = propsIn.get(key);
//                if (tagIn.isAbsent()) {
//                    continue outer;
//                }
//
//                if (value.equals("*")) {
//                    name = name.replace("*", tagIn.asString().getValue());
//                    continue;
//                }
//
//                if (!tagIn.getValue().equals(value)) {
//                    continue outer;
//                }
//            }
//
//            matchOut(state, name, propsOut, consumer);
//        }
    }

    private void matchOut(CompoundTag stateIn, String nameOut, CompoundTag matchProps, Consumer<String> consumer) {
        List<CompoundTag> statesOut = to.getStates(nameOut);
        if (statesOut.isEmpty()) {
            return;
        }

        CompoundTag propsIn = stateIn.getCompound("Properties").copy();
        CompoundTag defaultsOut = to.getDefault(nameOut).getCompound("Properties");
        if (defaultsOut.isPresent()) {
            for (Map.Entry<String, Tag> entry : defaultsOut) {
                if (propsIn.get(entry.getKey()).isAbsent()) {
                    propsIn.put(entry.getKey(), entry.getValue());
                }
            }
        }

        outer:
        for (CompoundTag state : statesOut) {
            CompoundTag propsOut = state.getCompound("Properties");
            for (Map.Entry<String, Tag> e : propsOut.getBacking().entrySet()) {
                String key = e.getKey();
                String value = e.getValue().asString().getValue();
                Tag matchValue = matchProps.get(key);
                if (matchValue.isPresent() && !matchValue.getValue().equals(value)) {
                    continue outer;
                }

                Tag inValue = propsIn.get(key);
                if (inValue.isPresent() && !inValue.getValue().equals(value)) {
                    continue outer;
                }
            }

            consumer.accept(Serializer.serialize(stateIn) + " -> " + Serializer.serialize(state));
            return;
        }
    }
}
