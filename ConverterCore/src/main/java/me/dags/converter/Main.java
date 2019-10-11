package me.dags.converter;

import me.dags.converter.config.Config;
import me.dags.converter.config.CustomData;
import me.dags.converter.data.GameData;
import me.dags.converter.data.Mappings;
import me.dags.converter.extent.Format;
import me.dags.converter.converter.Converter;
import me.dags.converter.converter.ExtentConverter;
import me.dags.converter.converter.ReaderFactory;
import me.dags.converter.converter.WriterFactory;
import me.dags.converter.converter.DirectoryConverter;
import me.dags.converter.extent.world.World;
import me.dags.converter.data.GameDataUtil;
import me.dags.converter.util.IO;
import me.dags.converter.util.Threading;
import me.dags.converter.util.log.Logger;
import me.dags.converter.version.Version;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Main {

    private static boolean headless = false;

    public static boolean isHeadless() {
        return headless;
    }

    private static boolean nogui(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-nogui")) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            if (IO.isJar()) {
                Logger.add(new PrintStream(IO.logFile()));
            }

            Logger.log("Running from directory:", new File("").getAbsolutePath());
            Logger.log("CPU Cores:", Threading.coreCount());
            Logger.log("Max Memory:", Threading.maxeMemory());
            Logger.flush();

            if (GraphicsEnvironment.isHeadless() || nogui(args)) {
                headless = true;
                HeadlessConverter.run(args);
            } else {
                headless = false;
                GUIConverter.run();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static File convert(Config config, Consumer<Float> progress) throws Exception {
        Logger.newLine();
        Logger.log("Running with config:");
        Logger.log(" - Input:");
        Logger.log("   + File:   ", config.input.file);
        Logger.log("   + Format: ", config.input.format);
        Logger.log("   + Version:", config.input.version);
        Logger.log(" - Output:");
        Logger.log("   + File:   ", config.output.file);
        Logger.log("   + Format: ", config.output.format);
        Logger.log("   + Version:", config.output.version);
        Logger.newLine();

        File source = config.input.file;
        File dest = getDestDir(source);

        Version from = config.input.version;
        Version to = config.output.version;
        Format formatIn = config.input.format;
        Format formatOut = config.output.format;

        try {
            if (formatIn == Format.WORLD) {
                return convertWorld(source, dest, from, to, config.custom, progress);
            } else {
                return convertExtent(source, dest, formatIn, formatOut, from, to, config.custom, progress);
            }
        } finally {
            Logger.flush();
        }
    }

    private static File convertWorld(File source, File dest, Version from, Version to, CustomData customData, Consumer<Float> progress) throws Exception {
        World world = new World(source, customData);
        List<Future<Void>> tasks = world.convert(from, to, dest);
        await(tasks, progress);
        return dest;
    }

    private static File convertExtent(File source, File dest, Format in, Format out, Version from, Version to, CustomData customData, Consumer<Float> progress) throws Exception {
        ReaderFactory reader = in.reader(from);
        WriterFactory writer = out.writer(to);
        Mappings mappings = Mappings.build(from, to).builtIn();
        GameData gameData = GameDataUtil.applyMappings(customData, mappings);
        Converter converter = new ExtentConverter(reader, writer, gameData, Collections.emptyList());
        List<Future<Void>> tasks = new DirectoryConverter(converter).convert(source, in, dest, out);
        await(tasks, progress);
        return dest;
    }

    private static void await(List<Future<Void>> tasks, Consumer<Float> progress) {
        float total = tasks.size();
        int lastSize = tasks.size();
        while (!tasks.isEmpty()) {
            tasks.removeIf(Future::isDone);
            int size = tasks.size();
            if (size == lastSize) {
                continue;
            }
            lastSize = size;
            progress.accept((total - size) / total);
        }
    }

    private static File getDestDir(File file) {
        File parent = file.getParentFile();
        String name = file.getName();
        if (!file.isDirectory()) {
            int index = name.lastIndexOf('.');
            name = name.substring(0, index);
        }
        return new File(parent, name + "-converted");
    }
}
