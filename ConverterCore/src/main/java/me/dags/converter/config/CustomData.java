package me.dags.converter.config;

import java.io.File;

public class CustomData {

    public final FileRef blocks = new FileRef();
    public final FileRef biomes = new FileRef();
    public final FileRef dataIn = new FileRef();
    public final FileRef dataOut = new FileRef();

    public static class FileRef {

        private File file = new File("");

        public File get() {
            return file;
        }

        public String getPath() {
            return get().getPath();
        }

        public void set(String path) {
            set(new File(path));
        }

        public void set(File file) {
            this.file = file;
        }
    }
}
