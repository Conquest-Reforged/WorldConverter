package me.dags.converter.util.log;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LoggingQueue implements ILogger {

    private static final long INTERVAL = 50L;

    private int max = 0;
    private final AtomicLong time = new AtomicLong(0);
    private final AtomicReference<String> footer = new AtomicReference<>();
    private final ConcurrentLinkedDeque<String> buffer = new ConcurrentLinkedDeque<>();
    private final AtomicReference<Consumer<String>> sink = new AtomicReference<>(s -> {});
    private final List<PrintStream> streams = Collections.synchronizedList(new LinkedList<>());

    void setSink(Consumer<String> consumer) {
        sink.set(consumer);
    }

    @Override
    public ILogger add(PrintStream stream) {
        streams.add(stream);
        return this;
    }

    @Override
    public ILogger log(Object line) {
        buffer.add(line.toString());
        return this;
    }

    @Override
    public ILogger flush() {
        drainBatch(buffer.size());
        return this;
    }

    @Override
    public ILogger footer(Object footer) {
        this.footer.set(footer.toString());
        update();
        return this;
    }

    private void update() {
        long now = System.currentTimeMillis();
        if (now - time.get() > INTERVAL) {
            max = Math.max(max, buffer.size());
            time.set(now);
            drainBatch(100);
        }
    }

    private void drainBatch(int count) {
        int drained = 0;

        for (int i = 0; i <= count; i++, drained++) {
            if (!drainOne(i == 0)) {
                break;
            }
        }

        applyFooter(drained > 0);
    }

    private boolean drainOne(boolean first) {
        String text = buffer.poll();
        if (text == null) {
            return false;
        }
        for (PrintStream ps : streams) {
            ps.println(text);
        }
        if (first) {
            System.out.print('\r');
        } else {
            System.out.print('\n');
        }
        System.out.print(text);
        sink.get().accept(text);
        return true;
    }

    private void applyFooter(boolean newLine) {
        String foot = footer.get();
        if (foot != null) {
            if (newLine) {
                System.out.print('\n');
            } else {
                System.out.print('\r');
            }
            System.out.print(foot);
        }
    }
}
