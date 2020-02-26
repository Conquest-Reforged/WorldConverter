package me.dags.converter.util;

import me.dags.converter.Main;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Threading {

    private static final ExecutorService executor = Threading.newCachedThreadPool();

    private static ExecutorService newCachedThreadPool() {
        int threads = Main.isHeadless() ? coreCount() : coreCount() - 1;
        return newCachedThreadPool(threads);
    }

    private static ExecutorService newCachedThreadPool(int threads) {
        ThreadPoolExecutor service = new ThreadPoolExecutor(threads, threads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        service.allowCoreThreadTimeOut(true);
        return service;
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    public static int coreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static long usedMemory() {
        return availableMemory() - freeMemory();
    }

    public static long freeMemory() {
        return Runtime.getRuntime().freeMemory() / 1000000L;
    }

    public static long availableMemory() {
        return Runtime.getRuntime().totalMemory() / 1000000L;
    }

    public static long maxeMemory() {
        return Runtime.getRuntime().maxMemory() / 1000000L;
    }

    public static float memoryUsage() {
        return (float) (usedMemory() / (double) availableMemory()) * 100F;
    }
}
