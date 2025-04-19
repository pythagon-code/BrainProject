/**
 * SimplexChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.channels;

import java.util.ArrayDeque;
import java.util.Queue;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public final class SimplexDataChannel implements SourceDataChannel, TargetDataChannel {
    public Queue<String> queue;
    public final int capacity;
    private Object messageAvailableMonitor;

    public SimplexDataChannel(int capacity) {
        queue = new ArrayDeque<>();
        this.capacity = capacity;
    }

    @Override
    public void registerMessageAvailableMonitor(Object monitor) {
        if (messageAvailableMonitor != null) {
            throw new IllegalStateException();
        }

        this.messageAvailableMonitor = monitor;
    }

    @Override
    public String removeMessage() {
        if (!hasMessage()) {
            throw new IllegalStateException();
        }

        return queue.remove();
    }
    
    @Override
    public boolean hasMessage() {
        if (!Thread.holdsLock(this)) {
            throw new IllegalMonitorStateException();
        }

        return !queue.isEmpty();
    }

    @Override
    public void addMessage(String message) {
        if (!hasSpace()) {
            throw new IllegalStateException();
        }

        queue.add(message);
        messageAvailableMonitor.notifyAll();
    }

    @Override
    public boolean hasSpace() {
        if (!Thread.holdsLock(this)) {
            throw new IllegalMonitorStateException();
        }
        
        return queue.size() < capacity;
    }
}