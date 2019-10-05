package me.dags.converter.resource.jar;

import me.dags.converter.resource.Resource;
import me.dags.converter.util.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarResource implements Resource {

    private final ZipFile jar;
    private final ZipEntry entry;

    JarResource(ZipFile jar, ZipEntry entry) {
        this.jar = jar;
        this.entry = entry;
    }

    @Override
    public String getPath() {
        return entry.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IO.buffer(jar.getInputStream(entry));
    }
}
