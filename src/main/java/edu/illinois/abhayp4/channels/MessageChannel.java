/**
 * MessageChannel.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public interface MessageChannel {
    void setMessageAvailableMonitor(Object monitor);
    void addMessage(String message);
    String removeMessage();
    boolean canAddMessage();
    boolean canRemoveMessage();
}
