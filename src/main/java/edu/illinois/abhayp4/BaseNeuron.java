/**
 * BaseNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import org.json.JSONObject;

final class BaseNeuron extends RelayNeuron {
    public BaseNeuron(
        String name, Source src1, Source src2, Target tgt1, Target tgt2
    ) {
        super(name, src1, src2, tgt1, tgt2);
    }

    @Override
    protected void deserialize(JSONObject data) {
        
    }

    @Override
    protected JSONObject serialize() {
        return null;
    }

    @Override
    protected void onReceiveFromTarget(boolean which, String message) {
        
    }
}