/**
 * DuplexChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public record DuplexDataChannel(SimplexDataChannel channel1, SimplexDataChannel channel2) {
    public DuplexDataChannel(int capacity) {
        this(new SimplexDataChannel(capacity), new SimplexDataChannel(capacity));
    }
}