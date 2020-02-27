package me.dags.tools.mappings;

import com.google.gson.JsonObject;
import me.dags.converter.block.BlockState;
import me.dags.converter.version.VersionData;
import me.dags.converter.version.versions.MinecraftVersion;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Converter {

    public static void main(String[] args) throws Throwable {
        Config config = new Config();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JProgressBar progress = new JProgressBar();
        progress.setMinimum(0);
        progress.setMaximum(1);

        JPanel bottomRow = new JPanel();
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.LINE_AXIS));
        bottomRow.add(progress);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(fileRow("Data In", f -> config.dataIn = f));
        root.add(fileRow("Data Out", f -> config.dataOut = f));
        root.add(fileRow("Mappings Json", f -> config.mappings = f));
        root.add(convert(config, (val, max) -> {
            if (max == -1) {
                progress.setValue(0);
                progress.setMaximum(1);
            } else {
                progress.setValue(progress.getValue() + val);
                progress.setMaximum(max);
                progress.repaint();
            }
        }));
        root.add(bottomRow);

        JFrame frame = new JFrame();
        frame.setTitle("Mappings Converter");
        frame.add(root);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static JComponent convert(Config config, BiConsumer<Integer, Integer> progress) {
        JButton button = new JButton("Convert");
        button.setPreferredSize(new Dimension(100, 30));
        button.addActionListener(e -> {
            if (config.dataIn == null || config.dataOut == null || config.mappings == null) {
                return;
            }

            button.setEnabled(false);
            button.setText("Converting...");

            ForkJoinPool.commonPool().submit(() -> {
                try {
                    VersionData in = loadData(config.dataIn);
                    VersionData out = loadData(config.dataOut);
                    Mappings mappings = new Mappings(config.mappings);
                    convert(config.mappings.getParentFile(), mappings, in, out, progress);
                    button.setEnabled(true);
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    button.setEnabled(true);
                    button.setText("Convert");
                    progress.accept(0, -1);
                }
            });
        });

        JPanel root = new JPanel();
        root.add(button);
        return root;
    }

    private static JComponent fileRow(String name, Consumer<File> consumer) {
        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(75, 30));

        JTextField field = new JTextField();
        field.setEditable(false);
        field.setPreferredSize(new Dimension(300, 30));

        JButton button = new JButton("Choose");
        button.setPreferredSize(new Dimension(100, 30));
        button.addActionListener(e -> picker(button, file -> {
            field.setText(file.getAbsolutePath());
            consumer.accept(file);
        }));

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(field);
        panel.add(button);

        return panel;
    }

    private static void picker(JComponent self, Consumer<File> consumer) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("").getAbsoluteFile());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = chooser.showOpenDialog(self);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            consumer.accept(file);
        }
    }

    private static VersionData loadData(File file) throws IOException {
        try {
            JsonObject data = JsonHelper.loadJson(file);
            String version = data.get("version").getAsString();
            return MinecraftVersion.parse(version).parseGameData(data);
        } catch (Throwable t) {
            throw new IOException(t);
        }
    }

    private static void convert(File dir, Mappings mappings, VersionData dataIn, VersionData dataOut, BiConsumer<Integer, Integer> progress) throws IOException {
        File fileOut = new File(dir, formatMappingsFile(dataIn, dataOut));
        try (Writer writer = new BufferedWriter(new FileWriter(fileOut))) {
            BlockRegistry fromBlocks = new BlockRegistry(dataIn.blocks);
            BlockRegistry toBlocks = new BlockRegistry(dataOut.blocks);

            progress.accept(0, mappings.getSize());

            mappings.forEach((from, to) -> {
                progress.accept(1, mappings.getSize());

                List<BlockState> in = fromBlocks.getStates(from.blockName.trim(), from.minData, from.maxData);
                List<BlockState> out = toBlocks.getStates(to.blockName.trim(), to.minData);
                if (in.size() != 1) {
                    if (from.blockName.contains("leaves2")) {
                        return;
                    }
                    throw new IOException("Missing input entry: " + from.blockName + "=" + "[" + from.minData + "]");
                }
                if (out.size() != 1) {
                    throw new IOException("Missing output entry: " + to.blockName + "=" + "[" + to.minData + "]");
                }
                writer.write(in.get(0).getIdentifier());
                writer.write(" -> ");
                writer.write(out.get(0).getIdentifier());
                writer.write("\n");
            });
        }
    }

    private static String formatMappingsFile(VersionData in, VersionData out) {
        return String.format("mappings-%s-%s.txt", in.version.getVersion(), out.version.getVersion());
    }

    private static class Config {

        private File dataIn = null;
        private File dataOut = null;
        private File mappings = null;
    }
}
