package me.dags.converter.extent.structure;

import org.jnbt.*;

import java.util.List;

public class StructureHelper {

    public static ListTag<Integer> vec(int x, int y, int z) {
        return Nbt.list(TagType.INT, x, y, z);
    }

    public static CompoundTag relativize(CompoundTag structure, StructureConfig config) {
        ListTag<Integer> size = structure.getListTag("size", TagType.INT);
        ListTag<CompoundTag> blocks = structure.getListTag("blocks", TagType.COMPOUND);

        int width = size.getBacking().get(0).getValue();
        int height = size.getBacking().get(1).getValue();
        int length = size.getBacking().get(2).getValue();

        int centerX = width / 2, centerZ = length / 2;
        int originX = 0, originY = Integer.MAX_VALUE, originZ = 0;
        for (CompoundTag block : blocks) {
            String name = block.getString("Name");
            if (!config.originBlocks.test(name)) {
                continue;
            }

            List<Tag<Integer>> pos = block.getListTag("pos", TagType.INT).getBacking();
            int y = pos.get(1).getValue();
            if (y < originY) {
                originY = y;
                originX = pos.get(0).getValue();
                originZ = pos.get(2).getValue();
            } else if (y == originY) {
                int x = pos.get(0).getValue();
                int z = pos.get(2).getValue();
                int dist0 = dist2(x, z, centerX, centerZ);
                int dist1 = dist2(originX, originZ, centerX, centerZ);
                if (dist0 < dist1) {
                    originX = x;
                    originZ = z;
                }
            }
        }

        if (originY > height) {
            originY = 0;
        }

        for (CompoundTag block : blocks) {
            List<Tag<Integer>> pos = block.getListTag("pos", TagType.INT).getBacking();
            int x = pos.get(0).getValue() - originX;
            int y = pos.get(1).getValue() - originY;
            int z = pos.get(2).getValue() - originZ;
            block.put("pos", vec(x, y, z));
        }

        return structure;
    }

    private static int dist2(int x0, int z0, int x1, int z1) {
        int dx = x0 - x1;
        int dz = z0 - z1;
        return dx * dx + dz * dz;
    }
}
