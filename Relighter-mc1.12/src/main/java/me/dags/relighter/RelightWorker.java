package me.dags.relighter;

import com.google.common.base.Stopwatch;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.WorldWorkerManager;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RelightWorker implements WorldWorkerManager.IWorker {

    private static final long INTERVAL = 15;
    private static final Set<Integer> activeIds = new HashSet<>();

    private final int id;
    private final World world;
    private final PosIterator iterator;
    private final WeakReference<ICommandSender> sender;
    private final Stopwatch elapsed = Stopwatch.createStarted();
    private final Stopwatch interval = Stopwatch.createStarted();

    public RelightWorker(World world, PosIterator pos, ICommandSender sender) {
        this.iterator = pos;
        this.world = world;
        this.sender = new WeakReference<>(sender);
        int id = 1;
        while (activeIds.contains(id)) {
            id++;
        }
        this.id = id;
        sendMessage("Created worker#%02d", id);
    }

    @Override
    public boolean hasWork() {
        if (!iterator.hasNext()) {
            activeIds.remove(id);
            sendMessage("Relighting complete!");
        }
        return iterator.hasNext();
    }

    @Override
    public void work() {
        if (iterator.next()) {
            Chunk chunk = world.getChunkFromChunkCoords(iterator.getX(), iterator.getY());
            chunk.generateSkylightMap();
            chunk.setLightPopulated(false);
            chunk.onTick(false);

            if (interval.elapsed(TimeUnit.SECONDS) >= INTERVAL) {
                interval.reset().start();
                sendMessage("[Worker#%02d] Relight Progress: %.2f%%, ETA: %s secs", id, getProgress(), getETA());
            }
        }
    }

    private float getProgress() {
        return 100 * iterator.progress();
    }

    private long getETA() {
        int processed = iterator.index();
        int remaining = iterator.total() - processed;
        long time = elapsed.elapsed(TimeUnit.SECONDS);
        double rate = processed / (double) time;
        return Math.round(remaining / rate);
    }

    private void sendMessage(String format, Object... args) {
        String message = String.format(format, args);
        ITextComponent text = new TextComponentString(message);
        ICommandSender target = sender.get();
        if (target != null) {
            target.sendMessage(text);
        }
        if (world.getMinecraftServer() != null && world.getMinecraftServer() != target) {
            world.getMinecraftServer().sendMessage(text);
        }
    }
}
