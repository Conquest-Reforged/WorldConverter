package me.dags.converter.converter.directory;

import me.dags.converter.converter.Converter;
import me.dags.converter.converter.ConverterData;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.converter.ReaderFactory;
import me.dags.converter.converter.WriterFactory;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;
import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import java.util.List;

public class ExtentConverter implements Converter {

    private final ReaderFactory readerFactory;
    private final WriterFactory writerFactory;
    private final List<DataConverter> converters;
    private final ConverterData converterData;

    public ExtentConverter(ReaderFactory readerFactory, WriterFactory writerFactory, ConverterData data, List<DataConverter> converters) {
        this.readerFactory = readerFactory;
        this.writerFactory = writerFactory;
        this.converters = converters;
        this.converterData = data;
    }

    @Override
    public CompoundTag convert(CompoundTag in) throws Exception {
        WriterConfig config = new WriterConfig();
        config.put("registry", converterData.blocks);
        config.put("source", in);
        config.put("Width", in.getInt("Width"));
        config.put("Height", in.getInt("Height"));
        config.put("Length", in.getInt("Length"));
        Extent.Reader reader = readerFactory.create(converterData.blocks, in);
        ConverterWriter writer = new ConverterWriter(writerFactory.create(config), converterData);
        reader.iterate(writer);
        Tag<?> ent = reader.getData("Entities");
        if (ent != null) {
            writer.setData("Entities", ent);
        }
        ent = reader.getData("TileEntities");
        if (ent != null) {
            writer.setData("TileEntities", ent);
        }
        DataConverter.writeData(reader, writer, converters);
        return writer.flush();
    }
}
