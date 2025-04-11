/**
 * BaseNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

final class BaseNeuron extends RelayNeuron {
    public BaseNeuron(
        String name, ModelClientService service, Source src1, Source src2, Target tgt1, Target tgt2
    ) {
        super(name, service, src1, src2, tgt1, tgt2);
    }

    @Override
    protected void onReceiveFromSource(boolean which, String message) {
        
    }
}