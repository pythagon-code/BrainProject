package edu.illinois.abhayp4;

public abstract class RelayNeuron implements Runnable {
    public final Channel.SourceEndpoint source1, source2;
    public final Channel.TargetEndpoint target1, target2;
    
    public RelayNeuron(
        Channel.SourceEndpoint src1,
        Channel.SourceEndpoint src2,
        Channel.TargetEndpoint tgt1,
        Channel.TargetEndpoint tgt2
    ) {
        source1 = src1;
        source2 = src2;
        target1 = tgt1;
        target2 = tgt2;
        
    }

    @Override
    public void run() {
        /// test
        source1.send("Hello");
        target1.receive();
    }
}