/**
 * MetaNeuron.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.neurons;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.abhayp4.channels.DuplexChannel;

sealed class MetaNeuron extends RelayNeuron permits ResponseNeuron {
    List<RelayNeuron> neurons;
    List<RelayNeuron> adjacentNeurons;
    
    public MetaNeuron() {
        neurons = new ArrayList<>();
    }

    public void setAdjacentNeuron(RelayNeuron other) {
        DuplexChannel channel = new DuplexChannel(10);
        addChannel(channel);
        other.addChannel(channel);
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
