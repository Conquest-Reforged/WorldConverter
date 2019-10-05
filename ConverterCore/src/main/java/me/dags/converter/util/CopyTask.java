package me.dags.converter.util;

import me.dags.converter.util.log.Logger;

import java.io.File;
import java.util.concurrent.Callable;

public class CopyTask implements Callable<Void> {

    private final File source;
    private final File dest;

    public CopyTask(File in, File out) {
        this.source = in;
        this.dest = out;
    }

    @Override
    public Void call() throws Exception {
        IO.copy(source, dest);
        Logger.log("Copied file:", source.getName());
        return null;
    }
}
