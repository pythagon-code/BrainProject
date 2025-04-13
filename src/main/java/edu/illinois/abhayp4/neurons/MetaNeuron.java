/**
 * MetaNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.neurons;

import java.util.ArrayList;
import java.util.List;

sealed class MetaNeuron extends RelayNeuron permits ResponseNeuron {
    private List<RelayNeuron> neurons;
    
    
    public MetaNeuron() {
        neurons = new ArrayList<>();
    }

    public void setAdjacentNeuron(RelayNeuron other) {
        
    }

    @Override
    protected void onMessageNotSent() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void onMessageReceived(int channelIdx, String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void onAwaken() {
        // TODO Auto-generated method stub
        
    }
}
