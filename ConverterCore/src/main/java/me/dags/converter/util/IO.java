package me.dags.converter.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.dags.converter.util.log.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;

public class IO {

    private static final File[] empty = new File[0];

    public static InputStream open(String resource) {
        return buffer(IO.class.getResourceAsStream(resource));
    }

    public static OutputStream buffer(OutputStream out) {
        if (out instanceof BufferedOutputStream) {
            return out;
        }
        return new BufferedOutputStream(out);
    }

    public static InputStream buffer(InputStream in) {
        if (in instanceof BufferedInputStream) {
            return in;
        }
        return new BufferedInputStream(in);
    }

    public static InputStream read(File file) throws IOException {
        return buffer(new FileInputStream(file));
    }

    public static OutputStream write(File file) throws IOException {
        makeFile(file.getAbsoluteFile());
        return buffer(new FileOutputStream(file));
    }

    public static String logFile() {
        return "world-converter.log";
    }

    public static boolean isJar() {
        URL url = IO.class.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(url.getPath());
        return !file.isDirectory();
    }

    public static File[] list(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return empty;
        }
        return files;
    }

    public static void makeDir(File dir) throws IOException {
        if (dir.mkdirs()) {
            Logger.log("Created directory:", dir);
        }
    }

    public static void makeFile(File file) throws IOException {
        if (!file.exists()) {
            makeDir(file.getParentFile());
            if (file.createNewFile()) {
                Logger.log("Created file:", file);
            }
        }
    }

    public static void copy(File fileIn, File fileOut) throws IOException {
        try (InputStream in = read(fileIn)) {
            copy(in, fileOut);
        };
    }

    public static void copy(InputStream in, File fileOut) throws IOException {
        try (OutputStream out = write(fileOut)) {
            copy(in, out);
        };
    }

    public static void copy(File fileIn, OutputStream out) throws IOException {
        try (InputStream in = read(fileIn)) {
            copy(in, out);
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int count;
        byte[] data = new byte[8192];
        while ((count = in.read(data, 0, data.length)) > 0) {
            out.write(data, 0, count);
        }
    }

    public static JsonElement loadJson(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return new JsonParser().parse(reader);
        }
    }
}
