/**
 * Source.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
sealed interface Source permits TextChannel {
    void setReceiveSignal(Object monitor);
    boolean hasMessage();
    String dequeue();
}