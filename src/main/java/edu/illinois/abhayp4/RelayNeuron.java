/**
 * RelayNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.io.Closeable;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    //@JsonSubTypes.Type(value = ResponseNeuron.class),
    @JsonSubTypes.Type(value = MetaNeuron.class),
    @JsonSubTypes.Type(value = BaseNeuron.class)
})
abstract class RelayNeuron extends RootObject implements Runnable, Closeable {
    @JsonIgnore private final Object receiveSignal;
    protected final Target source1, source2;
    private final Source target1, target2;

    protected final ModelClient client;
    private final Thread thread;
    private boolean closed = false;

    public RelayNeuron(
        String name, ModelClientService service, Target src1, Target src2, Source tgt1, Source tgt2
    ) {
        super(name);
        receiveSignal = new Object();
        source1 = src1;
        source2 = src2;
        target1 = tgt1;
        target2 = tgt2;
        target1.setReceiveSignal(receiveSignal);
        target2.setReceiveSignal(receiveSignal);

        client = service.getAvailableClient();
        thread = new Thread(this, this + "-Thread");
        thread.setDaemon(false);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        do {
            synchronized (receiveSignal) {
                try {
                    receiveSignal.wait();
                    if (target1.hasMessage()) {
                        onReceiveFromTarget(false, target1.dequeue());
                    }
                    else if (target2.hasMessage()) {
                        onReceiveFromTarget(true, target2.dequeue());
                    }
                }
                catch (InterruptedException e) {
                    GlobalState.waitForResumeSignal();
                }
            }
        } while (!closed);
    }

    @Override
    public void close() {
        closed = true;
        waitForThreadToEnd();
    }

    private void waitForThreadToEnd() {
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            throw new IllegalThreadStateException("Cannot interrupt during neuron close");
        }
    }

    protected abstract void onReceiveFromTarget(boolean whichSide, String message);
}