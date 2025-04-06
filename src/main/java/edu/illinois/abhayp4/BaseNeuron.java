/**
 * BaseNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.JsonProperty;
import com.fasterxml.jackson.JsonGetter;
import com.fasterxml.jackson.JsonSetter;

final class BaseNeuron extends RelayNeuron {
    public BaseNeuron(
        String name, Source src1, Source src2, Target tgt1, Target tgt2
    ) {
        super(name, src1, src2, tgt1, tgt2);
    }

    @Override
    protected void onReceiveFromTarget(boolean which, String message) {
        
    }
}