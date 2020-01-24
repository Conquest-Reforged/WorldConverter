package me.dags.converter.converter.directory;

import me.dags.converter.converter.Converter;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.converter.ReaderFactory;
import me.dags.converter.converter.WriterFactory;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;
import org.jnbt.CompoundTag;

import java.util.List;

public class ExtentConverter implements Converter {

    private final ReaderFactory readerFactory;
    private final WriterFactory writerFactory;
    private final List<DataConverter> converters;
    private final GameData gameData;

    public ExtentConverter(ReaderFactory readerFactory, WriterFactory writerFactory, GameData gameData, List<DataConverter> converters) {
        this.readerFactory = readerFactory;
        this.writerFactory = writerFactory;
        this.converters = converters;
        this.gameData = gameData;
    }

    @Override
    public CompoundTag convert(CompoundTag in) throws Exception {
        WriterConfig config = new WriterConfig();
        config.put("registry", gameData.blocks);
        config.put("source", in);
        config.put("Width", in.getInt("Width"));
        config.put("Height", in.getInt("Height"));
        config.put("Length", in.getInt("Length"));
        Extent.Reader reader = readerFactory.create(gameData.blocks, in);
        ConverterWriter writer = new ConverterWriter(writerFactory.create(config), gameData);
        reader.iterate(writer);
        writer.setData("Entities", reader.getData("Entities"));
        writer.setData("TileEntities", reader.getData("TileEntities"));
        DataConverter.writeData(reader, writer, converters);
        return writer.flush();
    }
}
