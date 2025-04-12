/**
 * DuplexChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public class DuplexChannel implements MessageChannel {
    private final MessageChannel channel1, channel2;
    private Thread thread1, thread2;

    public DuplexChannel(int capacity) {
        channel1 = new SimplexChannel(capacity);
        channel2 = new SimplexChannel(capacity);
    }

    @Override
    public void setMessageAvailableMonitor(Object monitor) {
        getChannel().setMessageAvailableMonitor(monitor);;
    }

    public void bindThread() {
        if (thread1 == null) {
            thread1 = Thread.currentThread();
        }
        else if (thread2 == null) {
            thread2 = Thread.currentThread();
        }
        else {
            throw new IllegalThreadStateException();
        }
    }

    @Override
    public void addMessage(String message) {
        getOtherChannel().addMessage(message);
    }

    @Override
    public String removeMessage() {
        return getChannel().removeMessage();
    }

    @Override
    public boolean canAddMessage() {
        return getOtherChannel().canAddMessage();
    }

    @Override
    public boolean canRemoveMessage() {
        return getChannel().canRemoveMessage();
    }

    private MessageChannel getChannel() {
        if (Thread.currentThread() == thread1) {
            return channel1;
        }
        else if (Thread.currentThread() == thread2) {
            return channel2;
        }
        else {
            throw new IllegalThreadStateException();
        }
    }

    private MessageChannel getOtherChannel() {
        if (Thread.currentThread() == thread1) {
            return channel2;
        }
        else if (Thread.currentThread() == thread2) {
            return channel1;
        }
        else {
            throw new IllegalThreadStateException();
        }
    }
}