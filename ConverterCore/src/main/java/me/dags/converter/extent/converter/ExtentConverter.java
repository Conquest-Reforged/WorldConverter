package me.dags.converter.extent.converter;

import me.dags.converter.data.GameData;
import me.dags.converter.extent.Extent;
import me.dags.converter.extent.WriterConfig;
import org.jnbt.CompoundTag;

import java.util.List;

public class ExtentConverter implements Converter {

    private final ReaderFunc readerFunc;
    private final WriterFunc writerFunc;
    private final GameData gameData;
    private final List<DataConverter> converters;

    public ExtentConverter(ReaderFunc readerFunc, WriterFunc writerFunc, GameData gameData, List<DataConverter> converters) {
        this.readerFunc = readerFunc;
        this.writerFunc = writerFunc;
        this.gameData = gameData;
        this.converters = converters;
    }

    @Override
    public CompoundTag convert(CompoundTag in) throws Exception {
        WriterConfig config = new WriterConfig();
        config.put("registry", gameData.blocks);
        config.put("source", in);
        config.put("Width", in.getInt("Width"));
        config.put("Height", in.getInt("Height"));
        config.put("Length", in.getInt("Length"));
        Extent.Reader reader = readerFunc.get(gameData.blocks, in);
        ConverterWriter writer = new ConverterWriter(writerFunc.get(config), gameData);
        reader.iterate(writer);
        writer.setData("Entities", reader.getData("Entities"));
        writer.setData("TileEntities", reader.getData("TileEntities"));
        DataConverter.writeData(reader, writer, converters);
        return writer.flush();
    }
}
