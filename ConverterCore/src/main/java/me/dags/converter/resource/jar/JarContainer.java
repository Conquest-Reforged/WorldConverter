package me.dags.converter.resource.jar;

import me.dags.converter.resource.Container;
import me.dags.converter.resource.Resource;
import me.dags.converter.util.log.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarContainer implements Container {

    private final ZipFile jar;

    public JarContainer(Path path) throws IOException {
        this.jar = new ZipFile(path.toString());
    }

    @Override
    public Resource getResource(Path path) {
        ZipEntry entry = jar.getEntry(path.toString());
        if (entry == null) {
            return Resource.NULL;
        }
        return new JarResource(jar, entry);
    }

    @Override
    public List<Resource> getResources(Path path) {
        String prefix = path.toString().replace("\\", "/");
        List<Resource> resources = new LinkedList<>();
        Enumeration<? extends ZipEntry> iterator = jar.entries();
        while (iterator.hasMoreElements()) {
            ZipEntry entry = iterator.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            if (entry.getName().startsWith(prefix)) {
                resources.add(new JarResource(jar, entry));
            }
        }
        resources = new ArrayList<>(resources);
        Collections.sort(resources);
        return resources;
    }

    @Override
    public void close() throws IOException {
        jar.close();
    }
}
