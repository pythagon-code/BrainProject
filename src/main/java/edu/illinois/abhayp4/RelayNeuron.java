/**
 * RelayNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.io.Closeable;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@name"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ResponseNeuron.class),
    @JsonSubTypes.Type(value = MetaNeuron.class),
    @JsonSubTypes.Type(value = BaseNeuron.class)
})
abstract class RelayNeuron implements Runnable, Closeable {
    @JsonIgnore private final Object receiveSignal;
    private final Source[]


    protected final ModelClient client;
    private final Thread thread;
    private boolean closed = false;

    public RelayNeuron(
        ModelClientService service, Source src1, Source src2, Target tgt1, Target tgt2
    ) {
        receiveSignal = new Object();
        
        source1.setReceiveSignal(receiveSignal);
        source2.setReceiveSignal(receiveSignal);

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
                    if (source1.hasMessage()) {
                        onReceiveFromSource(false, source1.dequeue());
                    }
                    else if (source2.hasMessage()) {
                        onReceiveFromSource(true, source2.dequeue());
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

    protected abstract void onReceiveFromSource(boolean whichSide, String message);
}