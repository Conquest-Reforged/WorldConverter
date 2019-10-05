package me.dags.converter.extent.converter;

import me.dags.converter.block.BlockState;
import me.dags.converter.data.GameData;
import me.dags.converter.extent.WriterConfig;
import me.dags.converter.extent.chunk.Chunk;
import me.dags.converter.extent.chunk.ChunkData;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.version.Version;
import org.jnbt.CompoundTag;

import java.util.List;

public class ChunkConverter implements Converter {

    private final List<DataConverter> section;
    private final List<DataConverter> level;
    private final GameData gameData;
    private final Version from;
    private final Version to;
    private final WriterConfig config;

    public ChunkConverter(Version from, Version to, GameData gameData) {
        this.level = determineLevelConversion(from, to, gameData);
        this.section = ChunkData.sectionData();
        this.gameData = gameData;
        this.from = from;
        this.to = to;
        this.config = new WriterConfig();
        config.put("registry", gameData.blocks);
    }

    @Override
    public CompoundTag convert(CompoundTag in) throws Exception {
        Chunk.Reader reader = from.chunkReader(gameData.blocks, in);
        Chunk.Writer writer = to.chunkWriter(config);

        int sections = reader.getSectionCount();
        for (int i = 0; i < sections; i++) {
            Volume.Reader sectionReader = reader.getSection(i);
            Volume.Writer sectionWriter = writer.getSection(i);
            writeSection(sectionReader, sectionWriter);
            DataConverter.writeData(sectionReader, sectionWriter, section);
        }

        DataConverter.writeData(reader, writer, level);
        return writer.flush();
    }

    private void writeSection(Volume.Reader reader, Volume.Writer writer) {
        if (reader.size() == 0) {
            return;
        }
        // blocks
        for (int y = 0; y < reader.getHeight(); y++) {
            for (int z = 0; z < reader.getLength(); z++) {
                for (int x = 0; x < reader.getWidth(); x++) {
                    BlockState state = reader.getState(x, y, z);
                    writer.setState(x, y, z, state);
                }
            }
        }
    }

    private static List<DataConverter> determineLevelConversion(Version from, Version to, GameData data) {
        if (from.isLegacy()) {
            if (to.isLegacy()) {
                return ChunkData.legacyLevel();
            }
            return ChunkData.legacyToLatestLevel(data);
        } else {
            if (to.isLegacy()) {
                return ChunkData.latestLegacyLevel(data);
            }
            return ChunkData.latestLevel();
        }
    }
}
