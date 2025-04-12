/**
 * RelayNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.neurons;

import java.io.Closeable;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.illinois.abhayp4.channels.DuplexChannel;
import edu.illinois.abhayp4.channels.MessageChannel;
import edu.illinois.abhayp4.client.ModelClient;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@name"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BaseNeuron.class),
    @JsonSubTypes.Type(value = MetaNeuron.class),
    @JsonSubTypes.Type(value = ResponseNeuron.class)
})
@JsonIdentityInfo(
    generator = ObjectIdGenerators.IntSequenceGenerator.class,
    property = "@id"
)
sealed abstract class RelayNeuron implements Runnable, Closeable permits MetaNeuron {
    protected final List<DuplexChannel> channels;
    private final Thread thread;
    private final ModelClient modelClient;
    private boolean awaken = false;
    private boolean done = false;

    public RelayNeuron() {
        channels = new ArrayList<>();

        thread = new Thread(this, "RelayNeuron-NeuronThread");
        thread.setDaemon(false);

        modelClient = null;
        done = false;
    }

    public void addChannel(DuplexChannel channel) {
        if (channels.contains(channel)) {
            throw new IllegalArgumentException();
        }

        channels.add(channel);
    }

    public void start() {
        thread.start();
    }

    public void awake() {
        if (Thread.currentThread() == thread) {
            throw new IllegalThreadStateException();
        }
        
        synchronized (this) {
            awaken = true;
            notifyAll();
        }
    }

    protected void sendMessage(int channelIdx, String message) {
        MessageChannel channel = channels.get(channelIdx);
        synchronized (channel) {
            if (channel.canAddMessage()) {
                channel.addMessage(message);
            }
            else {
                onMessageNotSent();
            }
        }
    }

    @Override
    public void run() {
        for (DuplexChannel channel : channels) {
            channel.bindThread();
        }

        do {
            synchronized (this) {
                try {
                    for (int i = 0; i < channels.size(); i++) {
                        MessageChannel channel = channels.get(i);
                        synchronized (channel) {
                            while (channel.canRemoveMessage()) {
                                onMessageReceived(i, channel.removeMessage());
                                awaken = false;
                            }
                        }
                    }
                    if (awaken) {
                        onAwaken();
                    }
                    awaken = false;
                    wait();
                }
                catch (InterruptedException e) { }
                finally {
                    awaken = false;
                }

            }
        } while (!done);
    }

    @Override
    public synchronized void close() {
        done = true;
        try {
            notifyAll();
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            throw new IllegalThreadStateException(e.getMessage());
        }
    }

    protected abstract void onMessageNotSent();

    protected abstract void onMessageReceived(int channelIdx, String message);

    protected abstract void onAwaken();
}
