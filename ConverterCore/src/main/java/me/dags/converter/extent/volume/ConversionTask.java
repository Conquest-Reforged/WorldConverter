package me.dags.converter.extent.volume;

import me.dags.converter.extent.converter.Converter;
import me.dags.converter.util.IO;
import me.dags.converter.util.log.Logger;
import org.jnbt.CompoundTag;
import org.jnbt.Nbt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ConversionTask implements Callable<Void> {

    private final File in;
    private final File out;
    private final Converter converter;

    public ConversionTask(File in, File out, Converter converter) {
        this.in = in;
        this.out = out;
        this.converter = converter;
    }

    @Override
    public Void call() throws Exception {
        try (InputStream inputStream = new GZIPInputStream(IO.read(in))) {
            CompoundTag input = Nbt.read(inputStream).getTag().asCompound();
            if (input.isAbsent()) {
                throw new IOException("Invalid nbt: " + in);
            }

            CompoundTag output = converter.convert(input);
            try (OutputStream outputStream = new GZIPOutputStream(IO.write(out))) {
                Nbt.write(output, outputStream);
            }

            Logger.log("Converted file:", in.getName());
        } catch (Throwable t) {
            Logger.log(in.getPath(), t.getMessage());
        }
        return null;
    }
}
