package me.dags.converter.converter.world;

import me.dags.converter.block.BlockState;
import me.dags.converter.converter.Converter;
import me.dags.converter.converter.DataConverter;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.chunk.ChunkData;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import me.dags.converter.version.Version;
import org.jnbt.CompoundTag;

import java.util.List;

public class ChunkConverter implements Converter {

    private final List<DataConverter> section;
    private final List<DataConverter> level;
    private final WriterConfig config;
    private final GameData gameData;
    private final Version from;
    private final Version to;

    public ChunkConverter(Version from, Version to, GameData gameData) {
        this.level = determineLevelConversion(from, to, gameData);
        this.section = ChunkData.sectionData();
        this.config = new WriterConfig();
        this.gameData = gameData;
        this.from = from;
        this.to = to;
        config.put("registry", gameData.blocks);
    }

    @Override
    public CompoundTag convert(CompoundTag in) throws Exception {
        Chunk.Reader reader = from.getChunkFormat().newReader(gameData.blocks, in);
        Chunk.Writer writer = to.getChunkFormat().newWriter(to, config);

        int sections = reader.getSectionCount();
        for (int i = 0; i < sections; i++) {
            Volume.Reader sectionReader = reader.getSection(i);
            Volume.Writer sectionWriter = writer.getSection(i);
            convertSection(i, reader, writer);
            DataConverter.writeData(sectionReader, sectionWriter, section);
        }

        DataConverter.writeData(reader, writer, level);
        return writer.flush();
    }

    private void convertSection(int index, Chunk.Reader chunkReader, Chunk.Writer chunkWriter) throws Exception {
        Volume.Reader reader = chunkReader.getSection(index);
        if (reader.size() == 0) {
            return;
        }

        Volume.Writer writer = chunkWriter.getSection(index);

        int blockY = index << 4;
        Registry.Parser<BlockState> parser = gameData.blocks.getParser();

        // blocks
        for (int y = 0; y < reader.getHeight(); y++) {
            for (int z = 0; z < reader.getLength(); z++) {
                for (int x = 0; x < reader.getWidth(); x++) {
                    BlockState stateIn = reader.getState(x, y, z);
                    stateIn = stateIn.getActualState(parser, chunkReader, x, blockY + y, z);

                    BlockState stateOut = gameData.blocks.getOutput(stateIn);
                    writer.setState(x, y, z, stateOut);
                    if (stateIn.requiresUpgrade()) {
                        chunkWriter.markUpgrade(index, x, y, z);
                    }
                }
            }
        }
    }

    private static List<DataConverter> determineLevelConversion(Version from, Version to, GameData data) {
        if (from.isLegacy()) {
            if (to.isLegacy()) {
                return ChunkData.legacyLevel();
            }
            return ChunkData.legacyToLatestLevel(from, to, data);
        } else {
            if (to.isLegacy()) {
                return ChunkData.latestLegacyLevel(from, to, data);
            }
            return ChunkData.v114Tov115(from, to, data);
        }
    }
}
