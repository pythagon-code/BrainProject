package edu.illinois.abhayp4;

public class UnitNeuron extends RelayNeuron {
    public UnitNeuron(
        TextChannel.SourceEndpoint src1,
        TextChannel.SourceEndpoint src2,
        TextChannel.TargetEndpoint tgt1,
        TextChannel.TargetEndpoint tgt2
    ) {
        super(src1, src2, tgt1, tgt2);
    }
}