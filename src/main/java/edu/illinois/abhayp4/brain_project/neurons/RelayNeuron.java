/**
 * RelayNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.neurons;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.illinois.abhayp4.brain_project.channels.SourceDataChannel;
import edu.illinois.abhayp4.brain_project.channels.TargetDataChannel;
import edu.illinois.abhayp4.brain_project.workers.ModelWorker;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BaseNeuron.class),
    @JsonSubTypes.Type(value = MetaNeuron.class),
    @JsonSubTypes.Type(value = ResponseNeuron.class)
})
sealed abstract class RelayNeuron implements Runnable, Closeable permits MetaNeuron {
    private final List<SourceDataChannel> sources;
    private final List<TargetDataChannel> targets;
    private final Thread thread;
    private final ModelWorker modelClient;
    private boolean awaken = false;
    private boolean done = false;

    public RelayNeuron() {
        sources = new ArrayList<>();
        targets = new ArrayList<>();

        thread = new Thread(this, "RelayNeuron-NeuronThread");
        thread.setDaemon(false);

        modelClient = null;
        done = false;
    }

    public void addSource(SourceDataChannel source) {
        if (sources.contains(source)) {
            throw new IllegalArgumentException();
        }

        sources.add(source);
    }

    public void addTarget(TargetDataChannel target) {
        if (targets.contains(target)) {
            throw new IllegalArgumentException();
        }

        targets.add(target);
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
        TargetDataChannel target = targets.get(channelIdx);
        synchronized (target) {
            if (target.hasSpace()) {
                target.addMessage(message);
            }
            else {
                onMessageNotSent();
            }
        }
    }

    @Override
    public void run() {
        for (SourceDataChannel source : sources) {
            source.registerMessageAvailableMonitor(this);
        }

        do {
            synchronized (this) {
                try {
                    for (int i = 0; i < sources.size(); i++) {
                        SourceDataChannel source = sources.get(i);
                        synchronized (source) {
                            while (source.hasMessage()) {
                                onMessageReceived(i, source.removeMessage());
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
