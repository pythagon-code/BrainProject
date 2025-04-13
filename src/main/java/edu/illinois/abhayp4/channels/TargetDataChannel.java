/**
 * TargetChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public sealed interface TargetDataChannel permits SimplexChannel {
    void addMessage(String message);
    boolean hasSpace();
}
