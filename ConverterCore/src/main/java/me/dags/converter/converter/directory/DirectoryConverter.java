package me.dags.converter.converter.directory;

import me.dags.converter.converter.config.ExtentFile;
import me.dags.converter.converter.Converter;
import me.dags.converter.extent.Format;
import me.dags.converter.extent.volume.ConversionTask;
import me.dags.converter.util.Threading;
import me.dags.converter.util.log.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class DirectoryConverter {

    private final Converter converter;

    public DirectoryConverter(Converter converter) {
        this.converter = converter;
    }

    public List<Future<Void>> convert(File source, Format sourceFormat, File dest, Format destFormat) {
        List<Future<Void>> results = new LinkedList<>();
        convert(source, sourceFormat, dest, destFormat, results, 0);
        return results;
    }

    private void convert(File source, Format sourceFormat, File dest, Format destFormat, List<Future<Void>> results, int depth) {
        if (++depth > 10){
            return;
        }
        if (source.isDirectory()) {
            File[] files = source.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                File destDir = dest;
                if (f.isDirectory()) {
                    destDir = new File(dest, f.getName());
                }
                convert(f, sourceFormat, destDir, destFormat, results, depth);
            }
        } else if (source.getName().endsWith(sourceFormat.getIdentifier())) {
            ExtentFile in = new ExtentFile(source, sourceFormat);
            ExtentFile out = ExtentFile.of(dest, source, sourceFormat, destFormat);
            Callable<Void> task = new ConversionTask(in.getFile(), out.getFile(), converter);
            results.add(Threading.submit(task));
            Logger.log("Queuing conversion:", source);
        } else {
            Logger.log("Skipping unknown file format:", source);
        }
    }
}
