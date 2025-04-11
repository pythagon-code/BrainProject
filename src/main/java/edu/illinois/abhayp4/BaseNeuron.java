/**
 * BaseNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

final class BaseNeuron extends RelayNeuron {
    public BaseNeuron(
        String name, ModelClientService service, Target src1, Target src2, Source tgt1, Source tgt2
    ) {
        super(name, service, src1, src2, tgt1, tgt2);
    }

    @Override
    protected void onReceiveFromTarget(boolean which, String message) {
        
    }
}