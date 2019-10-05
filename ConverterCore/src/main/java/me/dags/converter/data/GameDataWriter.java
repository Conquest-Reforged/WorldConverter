package me.dags.converter.data;

import com.google.gson.stream.JsonWriter;
import me.dags.converter.data.biome.BiomeData;
import me.dags.converter.data.block.BlockData;
import me.dags.converter.data.writer.DataWriter;
import me.dags.converter.data.writer.JsonDataWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameDataWriter implements AutoCloseable {

    private final Schema schema;
    private final DataWriter writer;

    private boolean open = false;

    public GameDataWriter(Schema schema) throws IOException {
        this(schema, new File("game_data.json"));
    }

    public GameDataWriter(Schema schema, File file) throws IOException {
        this.schema = schema;
        this.writer = new JsonDataWriter(new JsonWriter(new FileWriter(file)));
    }

    private void ensureOpen() throws IOException {
        if (!open) {
            open = true;
            writer.beginObject();
            writer.name("version").value(schema.version);
        }
    }

    public SectionWriter<BiomeData> startBiomes() throws IOException {
        ensureOpen();
        return new SectionWriter<>("biomes", writer, schema.biome);
    }

    public SectionWriter<BlockData> startBlocks() throws IOException {
        ensureOpen();
        return new SectionWriter<>("blocks", writer, schema.block);
    }

    @Override
    public void close() throws Exception {
        if (open) {
            open = false;
            writer.endObject().close();
        }
    }
}
