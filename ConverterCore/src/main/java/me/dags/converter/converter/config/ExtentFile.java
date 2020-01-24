package me.dags.converter.converter.config;

import me.dags.converter.extent.Format;

import java.io.File;

public class ExtentFile {

    private final File file;
    private final Format format;

    public ExtentFile(File file, Format format) {
        this.file = file;
        this.format = format;
    }

    public File getFile() {
        return file;
    }

    public Format getFormat() {
        return format;
    }

    public static ExtentFile of(File in) {
        for (Format format : Format.values()) {
            if (format.hasSuffix(in.getName())) {
                return new ExtentFile(in.getAbsoluteFile(), format);
            }
        }
        return null;
    }

    public static ExtentFile of(File dir, File file, Format in, Format out) {
        String suffix = in.getSuffix(file.getName());
        String name = file.getName().replace(suffix, out.getIdentifier());
        name = toSafeName(name, out.getIdentifier());
        File output = new File(dir, name);
        return new ExtentFile(output, out);
    }

    private static String toSafeName(String name, String extension) {
        int point = name.lastIndexOf('.');
        if (point > 0) {
            name = name.substring(0, point);
        }

        StringBuilder sb = new StringBuilder();
        char previous = (char) -1;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (c == ' ') {
                if (previous == '_') {
                    continue;
                }
                c = '_';
            }

            if (c != '_' && c != '.' && c != '/' && !Character.isAlphabetic(c) && !Character.isDigit(c)) {
                continue;
            }

            if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
            }

            sb.append(c);
            previous = c;
        }

        return sb.append('.').append(extension).toString();
    }
}
