package me.dags.converter.resource.dir;

import me.dags.converter.resource.Resource;
import me.dags.converter.util.IO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileResource implements Resource {

    private final Path path;

    FileResource(Path path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path.toString();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IO.buffer(Files.newInputStream(path));
    }
}
