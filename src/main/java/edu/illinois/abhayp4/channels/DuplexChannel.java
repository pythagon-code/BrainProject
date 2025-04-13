/**
 * DuplexChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public record DuplexChannel(SimplexChannel channel1, SimplexChannel channel2) {
    public DuplexChannel(int capacity) {
        this(new SimplexChannel(capacity), new SimplexChannel(capacity));
    }
}