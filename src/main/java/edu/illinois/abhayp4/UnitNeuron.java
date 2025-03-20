package edu.illinois.abhayp4;

public class UnitNeuron extends RelayNeuron {
    public UnitNeuron(
        Channel.SourceEndpoint src1,
        Channel.SourceEndpoint src2,
        Channel.TargetEndpoint tgt1,
        Channel.TargetEndpoint tgt2
    ) {
        super(src1, src2, tgt1, tgt2);
    }
}