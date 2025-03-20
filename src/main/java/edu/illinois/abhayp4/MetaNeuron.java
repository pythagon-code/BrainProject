package edu.illinois.abhayp4;

public class MetaNeuron extends RelayNeuron {
    public final RelayNeuron neuron1, neuron2, neuron3, neuron4;
    
    public MetaNeuron(
        int level,
        Channel.SourceEndpoint src1,
        Channel.SourceEndpoint src2,
        Channel.TargetEndpoint tgt1,
        Channel.TargetEndpoint tgt2 
    ) {
        super(src1, src2, tgt1, tgt2);

        if (level == 1) {
            neuron1 = new UnitNeuron(src1, src2, tgt1, tgt2);
            neuron2 = new UnitNeuron(src1, src2, tgt1, tgt2);
            neuron3 = new UnitNeuron(src1, src2, tgt1, tgt2);
            neuron4 = new UnitNeuron(src1, src2, tgt1, tgt2);
        } else {
            neuron1 = new MetaNeuron(level - 1, src1, src2, tgt1, tgt2);
            neuron2 = new MetaNeuron(level - 1, src1, src2, tgt1, tgt2);
            neuron3 = new MetaNeuron(level - 1, src1, src2, tgt1, tgt2);
            neuron4 = new MetaNeuron(level - 1, src1, src2, tgt1, tgt2);
        }
    }
}
