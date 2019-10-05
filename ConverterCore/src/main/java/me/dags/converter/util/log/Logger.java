package me.dags.converter.util.log;

import java.io.PrintStream;
import java.util.function.Consumer;

public class Logger {

    private static final LoggingQueue queue = new LoggingQueue();

    public static ILogger sink(Consumer<String> consumer) {
        queue.setSink(consumer);
        return queue;
    }

    public static ILogger add(PrintStream stream) {
        return queue.add(stream);
    }

    public static ILogger log(Object text) {
        return queue.log(text);
    }

    public static ILogger log(Object... args) {
        return queue.log(args);
    }

    public static ILogger log(String message, Throwable error) {
        return queue.log(message, error);
    }

    public static ILogger log(Throwable error) {
        return queue.log(error);
    }

    public static ILogger logf(String format, Object... args) {
        return log(String.format(format, args));
    }

    public static ILogger newLine() {
        return log("");
    }

    public static ILogger flush() {
        return queue.flush();
    }

    public static ILogger footer(String footer) {
        return queue.footer(footer);
    }
}
