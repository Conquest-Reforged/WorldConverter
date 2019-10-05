package me.dags.converter.data.mappings;

import me.dags.converter.version.MinecraftVersion;

import java.io.*;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader("mappings.txt")))) {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("mappings-out.txt")))) {
                StateRegistry from = StateRegistry.createLegacy(MinecraftVersion.V1_12.loadGameDataJson().getAsJsonObject("blocks"));
                StateRegistry to = StateRegistry.create(MinecraftVersion.V1_14.loadGameDataJson().getAsJsonObject("blocks"));
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
