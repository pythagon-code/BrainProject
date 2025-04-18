/**
 * TargetChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public sealed interface TargetDataChannel permits SimplexDataChannel {
    void addMessage(String message);
    boolean hasSpace();
}
