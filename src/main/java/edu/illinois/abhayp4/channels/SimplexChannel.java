/**
 * SimplexChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.channels;

import java.util.ArrayDeque;
import java.util.Queue;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public class SimplexChannel implements MessageChannel {
    public Queue<String> queue;
    public final int capacity;
    private Object messageAvailableMonitor;

    public SimplexChannel(int capacity) {
        queue = new ArrayDeque<>();
        this.capacity = capacity;
    }

    @Override
    public void setMessageAvailableMonitor(Object monitor) {
        if (monitor != null) {
            throw new IllegalThreadStateException();
        }

        this.messageAvailableMonitor = monitor;
    }

    @Override
    public void addMessage(String message) {
        
            if (!canAddMessage()) {
                throw new IllegalStateException();
            }

            queue.add(message);
            messageAvailableMonitor.notifyAll();
    }

    @Override
    public String removeMessage() {
        if (!canRemoveMessage()) {
            throw new IllegalStateException();
        }

        return queue.remove();
    }

    @Override
    public boolean canAddMessage() {
        if (!Thread.holdsLock(this)) {
            throw new IllegalMonitorStateException();
        }
        
        return queue.size() < capacity;
    }

    @Override
    public boolean canRemoveMessage() {
        if (!Thread.holdsLock(this)) {
            throw new IllegalMonitorStateException();
        }

        return !queue.isEmpty();
    }
}
