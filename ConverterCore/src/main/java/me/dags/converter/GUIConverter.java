package me.dags.converter;

import me.dags.converter.converter.config.Config;
import me.dags.converter.converter.config.CustomData;
import me.dags.converter.extent.ExtentType;
import me.dags.converter.extent.Format;
import me.dags.converter.resource.Container;
import me.dags.converter.resource.Resource;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import me.dags.converter.util.progress.ProgressBar;
import me.dags.converter.version.MinecraftVersion;
import me.dags.converter.version.Version;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class GUIConverter {

    private static final JTextField path = new JTextField();

    private static JComboBox<Format> inputFormat;
    private static JComboBox<Version> inputVersion;
    private static JComboBox<Format> outputFormat;
    private static JComboBox<Version> outputVersion;
    private static JTextField messages;

    public static void run() throws Throwable {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Config config = new Config();

        JFrame frame = new JFrame();
        frame.setTitle("Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(input(frame, config.input));
        root.add(new JSeparator());
        root.add(output(frame, config.output));
        root.add(new JSeparator());

        messages = new JTextField();
        messages.setEditable(false);

        JProgressBar progressBar = new JProgressBar();
        root.add(convert(frame, progressBar, config));
        root.add(progressBar);
        root.add(messages);

        frame.add(root);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel input(JFrame frame, Config.Data data) {
        int left = 40;
        int middle = 300;
        int right = 80;
        int height = 30;
        int width = left + middle + right;
        int optionWidth = (width - left - left) / 2;

        JLabel folder = new JLabel("Folder");
        folder.setPreferredSize(new Dimension(left, height));

        path.setEditable(false);
        path.setText(data.file.getPath());
        path.setPreferredSize(new Dimension(middle, height));

        JButton choose = new JButton("Choose");
        choose.setPreferredSize(new Dimension(right, height));
        choose.addActionListener(e -> SwingUtilities.invokeLater(() -> chooseFile(frame, path, data)));

        JLabel format = new JLabel("Input");
        format.setPreferredSize(new Dimension(left, height));
        inputFormat = select(Format.values(), data.format, optionWidth, height, f -> data.format = f);

        JLabel version = new JLabel("Version");
        version.setPreferredSize(new Dimension(left, height));
        inputVersion = select(MinecraftVersion.values(), data.version, optionWidth, height, v -> data.version = v);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        root.add(row(folder, path, choose));
        root.add(row(format, inputFormat, version, inputVersion));

        return root;
    }

    private static JPanel output(JFrame frame, Config.Data data) {
        int left = 40;
        int middle = 300;
        int right = 80;
        int height = 30;
        int width = left + middle + right;
        int optionWidth = (width - left - left) / 2;

        JLabel format = new JLabel("Output");
        format.setPreferredSize(new Dimension(left, height));
        outputFormat = select(Format.values(), data.format, optionWidth, height, f -> data.format = f);
        outputFormat.addActionListener(e -> {
            Format formatOut = outputFormat.getItemAt(outputFormat.getSelectedIndex());
            if (formatOut == Format.WORLD) {
                Format formatIn = inputFormat.getItemAt(inputFormat.getSelectedIndex());
                if (formatIn != Format.WORLD) {
                    outputFormat.setSelectedItem(formatIn);
                }
            }
        });

        JLabel version = new JLabel("Version");
        version.setPreferredSize(new Dimension(left, height));
        outputVersion = select(MinecraftVersion.values(), data.version, optionWidth, height, v -> data.version = v);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        root.add(row(format, outputFormat, version, outputVersion));

        return root;
    }

    private static JPanel convert(JFrame frame, JProgressBar progress, Config config) {
        int height = 30;
        int smallWidth = 85;
        int bigWidth = 250;

        JButton advanced = button("Advanced", smallWidth, height, () -> advanced(frame, config));

        JButton mods = button("Mods", smallWidth, height, () -> mods(frame, config));

        JButton convert = button("Convert", bigWidth, height, () -> {});
        convert.addActionListener(e -> {
            if (config.input.format == Format.NONE) {
                return;
            }
            convert.setEnabled(false);
            Config conf = config.copy();
            Runnable callback = () -> convert.setEnabled(true);
            new Thread(() -> run(frame, progress, conf, callback)).start();
        });

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        root.add(row(advanced, mods, convert));
        return root;
    }

    private static void advanced(JFrame frame, Config config) {
        CustomData customData = config.custom;
        JDialog dialog = new JDialog(frame, "Advanced Options");

        JButton cancel = button("Cancel", 120, 30, () -> {
            config.custom = new CustomData();
            dialog.dispose();
        });

        JButton done = button("Done", 120, 30, dialog::dispose);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        root.add(customDataRow(dialog, "Data In", customData.dataIn));
        root.add(customDataRow(dialog, "Data Out", customData.dataOut));
        root.add(customDataRow(dialog, "Block Mappings", customData.blocks));
        root.add(customDataRow(dialog, "Biome Mappings", customData.biomes));
        root.add(row(cancel, done));

        dialog.add(root);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
    }

    private static void mods(JFrame frame, Config config) {
        JDialog dialog = new JDialog(frame, "GameData Mods");

        List<String> jars = new LinkedList<>();
        try (Container container = Container.self(Main.class)) {
            for (Resource resource : container.getResources(Paths.get("mods"))) {
                jars.add(Paths.get(resource.getPath()).getFileName().toString());
            }
        } catch (IOException e) {
            Logger.log(e).flush();
        }


        String[] options = jars.isEmpty() ? new String[]{"none"} : jars.toArray(new String[0]);
        JComboBox<String> mods = select(options, options[0], 300, 30, s -> {});
        JButton extract = button("Extract", 100, 30, () -> {
            String option = mods.getItemAt(mods.getSelectedIndex());
            if (option.equals("none")) {
                return;
            }
            try (InputStream in = IO.open("/mods/" + option)) {
                IO.copy(in, new File(option));
                dialog.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        root.add(row(mods, extract));

        dialog.add(root);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
    }

    private static void run(JFrame frame, JProgressBar progress, Config config, Runnable callback) {
        try {
            progress.setMinimum(0);
            progress.setMaximum(100);
            Logger.sink(messages::setText);
            File result = Main.convert(config, ProgressBar.console().andThen(p -> {
                progress.setValue(Math.round(p * 100));
                progress.repaint();
            }));
            JOptionPane.showMessageDialog(frame, "Complete!");
            Desktop.getDesktop().open(result);
        } catch (Throwable e) {
            Logger.log(e).flush();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            JOptionPane.showMessageDialog(frame, sw.toString(), e.toString(), JOptionPane.ERROR_MESSAGE);
        } finally {
            Logger.sink(s -> {});
            progress.setValue(0);
            messages.setText("");
            callback.run();
        }
    }

    private static JPanel row(Component... components) {
        JPanel row = new JPanel();
        for (Component component : components) {
            row.add(component);
        }
        return row;
    }

    private static void choose(Component parent, Consumer<String> setter) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("").getParentFile());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            setter.accept(file.getAbsolutePath());
            setter.accept(file.getAbsolutePath());
        }
    }

    private static void chooseFile(JFrame parent, JTextField field, Config.Data data) {
        JFileChooser chooser = new JFileChooser();
        File current = data.file;
        if (current.getPath().isEmpty()) {
            current = new File("").getAbsoluteFile();
        }

        chooser.setCurrentDirectory(current);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            String fileType = chooser.getSelectedFile().isDirectory() ? "folder:" : "file:";
            Logger.log("Selected:", fileType, chooser.getSelectedFile().getAbsoluteFile());

            field.setText(chooser.getSelectedFile().getAbsolutePath());
            data.file = chooser.getSelectedFile().getAbsoluteFile();
            ExtentType type = ExtentType.guessType(data.file);

            if (type.isValid()) {
                Logger.log("Detected format:", type.a, "and Version:", type.b);

                inputFormat.setSelectedItem(type.a);
                inputVersion.setSelectedItem(type.b);
                outputFormat.setSelectedItem(type.a);

                if (type.a == Format.WORLD) {
                    outputFormat.setEnabled(false);
                } else {
                    outputFormat.setEnabled(true);
                }

                if (type.b == MinecraftVersion.V1_12) {
                    outputVersion.setSelectedItem(MinecraftVersion.V1_14);
                } else {
                    outputVersion.setSelectedItem(MinecraftVersion.V1_14);
                }
            }
        }
    }

    private static <T> JComboBox<T> select(T[] options, T selected, int width, int height, Consumer<T> consumer) {
        JComboBox<T> comboBox = new JComboBox<>(options);
        comboBox.setSelectedItem(selected);
        comboBox.setPreferredSize(new Dimension(width, height));
        comboBox.addActionListener(e -> {
            T t = comboBox.getItemAt(comboBox.getSelectedIndex());
            consumer.accept(t);
            Logger.log("Selected item:", t);
        });
        return comboBox;
    }

    private static Component customDataRow(JDialog dialog, String name, CustomData.FileRef fileRef) {
        JLabel label = label("Custom " + name + ":", 120, 25);

        JTextField field = field(fileRef.getPath(), 250, 25);
        field.setEditable(false);

        JButton choose = button("Choose", 80, 25, () -> choose(dialog, path -> {
            field.setText(path);
            fileRef.set(path);
        }));

        return row(label, field, choose);
    }

    private static JLabel label(String text, int width, int height) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(width, height));
        return label;
    }

    private static JTextField field(String text, int width, int height) {
        JTextField field = new JTextField(text);
        field.setPreferredSize(new Dimension(width, height));
        return field;
    }

    private static JButton button(String text, int width, int height, Runnable action) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.addActionListener(e -> action.run());
        return button;
    }
}
