package thing;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Nbt;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Registry {

    private final Map<String, Integer> blockIds;

    public Registry(File level) throws IOException {
        Map<String, Integer> blockIds = new HashMap<>();
        try (InputStream in = new BufferedInputStream(new FileInputStream(level))) {
            Tag<?> registry = Nbt.read(in).getTag().asCompound().get("FML", "Registries", "minecraft:blocks", "ids");
            if (registry.isAbsent()) {
                throw new RuntimeException("Block registry not found");
            }
            ListTag<CompoundTag> ids = registry.asList(TagType.COMPOUND);
            for (CompoundTag entry : ids) {
                String key = entry.getString("K");
                Integer value = entry.getInt("V");
                blockIds.put(key, value);
            }
        }
        this.blockIds = blockIds;
    }

    public int getStateId(String blockName, int data) {
        Integer id = blockIds.get(blockName);
        if (id == null) {
            throw new RuntimeException("Block id not found for block: " + blockName);
        }
        return getStateId(id, data);
    }

    public static int getStateId(int blockId, int meta) {
        return blockId + (meta << 12);
    }

    public static int getBlockId(int stateId) {
        return stateId & 4095;
    }

    public static int getMetaData(int stateId) {
        return (stateId >> 12) & 15;
    }
}
