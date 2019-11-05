package me.dags.converter;

import me.dags.converter.converter.Converter;
import me.dags.converter.converter.world.region.RegionTask;
import org.jnbt.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Replacer {

    public static void main(String[] args) throws Exception {
        File dir = new File("F:\\OneDrive\\Documents\\ArdaCraft Launcher\\instances\\ArdaCraft Modpack 1.12.2\\minecraft\\saves\\ReLight\\region");
        File outdir = new File(dir, "converted");

        ByteArrayTag light = Nbt.tag(new byte[2048]);

        visit(outdir, dir, chunk -> {
            CompoundTag level = chunk.getCompound("Level");
            ListTag<CompoundTag> sections = level.getListTag("Sections", TagType.COMPOUND);
            if (sections.isAbsent()) {
                return chunk;
            }

            List<CompoundTag> fixed = new LinkedList<>();
            for (CompoundTag section : sections) {
                CompoundTag out = section.copy();
                out.put("BlockLight", light);
                out.put("SkyLight", light);
                fixed.add(out);
            }

            level = level.copy();
            level.put("Sections", Nbt.list(TagType.COMPOUND, fixed));
            level.put("LightPopulated", 0);

            return chunk.copy().put("Level", level);
        });
    }

    public static void visit(File out, File file, Converter converter) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                visit(out, f, converter);
            }
        } else {
            File outFile = new File(out, file.getName());
            RegionTask task = new RegionTask(file, outFile, converter);
            task.call();
        }
    }
}
