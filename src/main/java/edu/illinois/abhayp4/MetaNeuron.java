/**
 * MetaNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
final class MetaNeuron extends RelayNeuron {
    @JsonProperty("Children") private final RelayNeuron[] children;
    
    private final TextChannel inner14;
    private final TextChannel inner43, inner32, inner21;
    private final TextChannel outer12Src, outer34Src;
    private final TextChannel outer1Tgt, outer2Tgt, outer3Tgt, outer4Tgt;

    public MetaNeuron(
        String name, Source src1, Source src2, Target tgt1, Target tgt2, int level
    ) {
        super(name, src1, src2, tgt1, tgt2);

        inner14 = new TextChannel(name + "-inner14");
        inner43 = new TextChannel(name + "-inner43");
        inner32 = new TextChannel(name + "-inner32");
        inner21 = new TextChannel(name + "-inner21");

        outer12Src = new TextChannel(name + "-outer12Src");
        outer34Src = new TextChannel(name + "-outer34Src");

        outer1Tgt = new TextChannel(name + "-outer1Tgt");
        outer2Tgt = new TextChannel(name + "-outer2Tgt");
        outer3Tgt = new TextChannel(name + "-outer3Tgt");
        outer4Tgt = new TextChannel(name + "-outer4Tgt");

        children = new RelayNeuron[4];
        if (level == 1) {
            children[0] = new BaseNeuron(name + "-1", outer12Src, inner14, outer1Tgt, inner21);
            children[1] = new BaseNeuron(name + "-2", outer12Src, inner21, outer2Tgt, inner32);
            children[2] = new BaseNeuron(name + "-3", outer34Src, inner32, outer3Tgt, inner43);
            children[3] = new BaseNeuron(name + "-4", outer34Src, inner43, outer4Tgt, inner14);
        } else {
            children[0] = new MetaNeuron(name + "-1", outer12Src, inner14, outer1Tgt, inner21, level - 1);
            children[1] = new MetaNeuron(name + "-2", outer12Src, inner21, outer2Tgt, inner32, level - 1);
            children[2] = new MetaNeuron(name + "-3", outer34Src, inner32, outer3Tgt, inner43, level - 1);
            children[3] = new MetaNeuron(name + "-4", outer34Src, inner43, outer4Tgt, inner14, level - 1);
        }
    }

    @Override
    public void run() {
        for (RelayNeuron child : children) {
            child.start();
        }
        super.run();
    }
    
    
    @Override
    public void close() {
        for (RelayNeuron child : children) {
            child.close();
        }
        super.close();
    }

    @Override
    protected void onReceiveFromTarget(boolean whichSide, String message) {
        // Will incorporate switch logic later
        if (!whichSide) {
            outer1Tgt.enqueue(message);;
            outer4Tgt.enqueue(message);
        }
        else {
            outer2Tgt.enqueue(message);
            outer3Tgt.enqueue(message);
        }
    }
}