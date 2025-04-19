package edu.illinois.abhayp4.brain_project.clock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private final List<Lock> locks;

    public LockManager() {
        locks = new ArrayList<>();
    }

    public Lock createLock() {
        locks.add(new ReentrantLock());
        return locks.getLast();
    }

    public void lockAll() {
        for (Lock lock : locks) {
            lock.lock();
        }
    }

    public void unlockAll() {
        for (Lock lock : locks) {
            lock.unlock();
        }
    }
}
