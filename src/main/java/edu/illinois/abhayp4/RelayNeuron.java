package edu.illinois.abhayp4;

public abstract class RelayNeuron implements Runnable {
    public final TextChannel.SourceEndpoint source1, source2;
    public final TextChannel.TargetEndpoint target1, target2;
    
    public RelayNeuron(
        TextChannel.SourceEndpoint src1,
        TextChannel.SourceEndpoint src2,
        TextChannel.TargetEndpoint tgt1,
        TextChannel.TargetEndpoint tgt2
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