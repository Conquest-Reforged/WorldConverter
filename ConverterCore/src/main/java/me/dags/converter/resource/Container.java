package me.dags.converter.resource;

import me.dags.converter.resource.dir.DirContainer;
import me.dags.converter.resource.jar.JarContainer;
import me.dags.converter.util.IO;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public interface Container extends AutoCloseable {

    Resource getResource(Path path);

    List<Resource> getResources(Path path);

    default boolean isPresent() {
        return true;
    }

    @Override
    default void close() throws IOException {

    }

    Container NULL = new Container() {
        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Resource getResource(Path path) {
            return Resource.NULL;
        }

        @Override
        public List<Resource> getResources(Path path) {
            return Collections.emptyList();
        }
    };

    static Container self(Class<?> source) throws IOException {
        URL location = source.getProtectionDomain().getCodeSource().getLocation();
        return open(location.getPath());
    }

    static Container open(String path) throws IOException {
        return Container.open(IO.toPath(path));
    }

    static Container open(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            return new DirContainer(path);
        }
        return new JarContainer(path);
    }
}
