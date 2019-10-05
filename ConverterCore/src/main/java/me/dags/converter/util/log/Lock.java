package me.dags.converter.util.log;

public class Lock implements AutoCloseable {

    private volatile boolean locked = false;

    public Lock lock() {
        while (!locked) {}
        locked = true;
        return this;
    }

    @Override
    public void close() {
        locked = false;
    }
}
