package edu.illinois.abhayp4.brain_project.clock;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;

public class Heartbeat implements Runnable, Closeable {
    private final LockManager lockManager;
    public volatile boolean suspended = false;
    private volatile boolean done = false;

    public Heartbeat() {
        lockManager = new LockManager();
    }

    public Lock createLock() {
        return lockManager.createLock();
    }

    @Override
    public void run() {
        do {
            lockManager.lockAll();

            do {
                Thread.yield();
            } while (suspended && !done);

            lockManager.unlockAll();
        } while (!done);
    }

    @Override
    public void close() {
        done = true;
    }
}