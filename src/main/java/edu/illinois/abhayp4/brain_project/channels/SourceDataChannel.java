/**
 * SourceChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public sealed interface SourceDataChannel permits SimplexChannel {
    void setMessageAvailableMonitor(Object monitor);
    String removeMessage();
    boolean hasMessage();
}