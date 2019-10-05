package me.dags.converter.resource.dir;

import me.dags.converter.resource.Container;
import me.dags.converter.resource.Resource;
import me.dags.converter.util.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DirContainer implements Container {

    private final Path root;

    public DirContainer(Path root) {
        if (root.endsWith("classes\\java\\main")) {
            this.root = root.getParent().getParent().getParent().resolve("resources").resolve("main");
        } else if (root.endsWith("classes")) {
            this.root = root.getParent().resolve("resources");
        } else {
            this.root = root;
        }
    }

    @Override
    public Resource getResource(Path path) {
        return new FileResource(root.resolve(path));
    }

    @Override
    public List<Resource> getResources(Path path) {
        List<Resource> resources = new LinkedList<>();
        int depth = path.toString().isEmpty() ? 1 : 50;
        visit(root.resolve(path), resources, -1, depth);
        resources = new ArrayList<>(resources);
        Collections.sort(resources);
        return resources;
    }

    @Override
    public void close() {

    }

    private static void visit(Path path, List<Resource> resources, int depth, final int maxDepth) {
        if (depth + 1 > maxDepth) {
            return;
        }
        if (!Files.exists(path)) {
            return;
        }
        if (Files.isDirectory(path)) {
            try {
                Files.list(path).forEach(p -> visit(p, resources, depth + 1, maxDepth));
            } catch (Throwable t) {
                Logger.log(t);
            }
        } else {
            resources.add(new FileResource(path));
        }
    }
}
