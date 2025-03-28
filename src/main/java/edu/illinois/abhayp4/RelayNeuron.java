/**
 * RelayNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.io.Closeable;
import java.io.IOError;

abstract class RelayNeuron extends NamedObject implements Runnable, Closeable {
    private final Object targetMonitor;
    protected final Source source1, source2;
    private final Target target1, target2;

    protected final ModelClient client;
    private final Thread thread;
    private boolean closed = false;

    public RelayNeuron(
        String name, Source src1, Source src2, Target tgt1, Target tgt2
    ) {
        super(name);
        targetMonitor = new Object();
        source1 = src1;
        source2 = src2;
        target1 = tgt1;
        target2 = tgt2;
        target1.setMonitor(targetMonitor);
        target2.setMonitor(targetMonitor);

        client = ModelClientService.getService().getAvailableClient();
        thread = new Thread(this);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        do {
            synchronized (targetMonitor) {
                try {
                    targetMonitor.wait();
                    if (target1.hasMessage()) {
                        onReceiveFromTarget(false, target1.remove());
                    }
                    else if (target2.hasMessage()) {
                        onReceiveFromTarget(true, target2.remove());
                    }
                }
                catch (InterruptedException e) { }
            }
        } while (!closed);
    }

    @Override
    public void close() {
        closed = true;
        client.close();
        waitForThreadToEnd(7, null);
    }

    private void waitForThreadToEnd(int triesLeft, Throwable prevException) {
        if (triesLeft == 0) {
            throw new IOError(prevException);
        }
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            waitForThreadToEnd(triesLeft - 1, prevException);
        }
    }

    protected abstract void onReceiveFromTarget(boolean whichSide, String message);
}