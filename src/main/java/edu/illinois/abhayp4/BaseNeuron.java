/**
 * BaseNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

public final class BaseNeuron extends RelayNeuron {
    public BaseNeuron(
        String name, Addable src1, Addable src2, Removable tgt1, Removable tgt2
    ) {
        super(name, src1, src2, tgt1, tgt2);
    }

    @Override
    protected void onReceiveFromTarget(boolean which, String message) {
        
    }
}