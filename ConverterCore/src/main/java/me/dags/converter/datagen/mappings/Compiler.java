package me.dags.converter.datagen.mappings;

import me.dags.converter.version.versions.MinecraftVersion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Compiler {

    public static void compile(String path) throws Exception {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(path)))) {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("mappings-compiled.txt")))) {
                StateRegistry from = StateRegistry.createLegacy(MinecraftVersion.V1_12.loadGameDataJson().getAsJsonObject("blocks"));
                StateRegistry to = StateRegistry.create(MinecraftVersion.V1_15.loadGameDataJson().getAsJsonObject("blocks"));
                StateMapper mapper = new StateMapper(from, to);
                boolean lastWasEmpty = false;
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.isEmpty() && !lastWasEmpty) {
                        writer.println();
                        lastWasEmpty = true;
                        continue;
                    }
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String[] parts = line.split("->");
                    if (parts.length != 2) {
                        continue;
                    }
                    Rule in = Rule.parse(parts[0].trim());
                    Rule out = Rule.parse(parts[1].trim());
                    mapper.map(in, out, writer::println);
                    lastWasEmpty = false;
                }
            }
        }
    }
}
