/**
 * TextChannel.java
 * @author Abhay
 */

package edu.illinois.abhayp4;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

final class TextChannel extends NamedObject implements Source, Target {
    private final Queue<String> messages;
    private Object receiveSignal;
    
    TextChannel(String name) {
        super(name);
        messages = new ArrayDeque<>();
    }

    @Override
    public void setReceiveSignal(Object receiveSignal) {
        if (this.receiveSignal != null) {
            throw new IllegalStateException("Monitor already exists");
        }

        this.receiveSignal = receiveSignal;
    }

    @Override
    public void enqueue(String message) {
        synchronized (receiveSignal) {
            messages.add(message);
            receiveSignal.notifyAll();
        }
    }

    @Override
    public boolean hasMessage() {
        if (!Thread.holdsLock(receiveSignal)) {
            throw new IllegalMonitorStateException("Thread must hold monitor lock for this operation");
        }

        return !messages.isEmpty();
    }

    @Override
    public String dequeue() {
        if (!Thread.holdsLock(receiveSignal)) {
            throw new IllegalMonitorStateException("Thread must hold monitor lock for this operation");
        }

        if (!hasMessage()) {
            throw new NoSuchElementException("No messages in queue");
        }

        return messages.remove();
    }
}