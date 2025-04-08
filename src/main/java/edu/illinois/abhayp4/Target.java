/**
 * Target.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
interface Target {
    void setReceiveSignal(Object monitor);
    boolean hasMessage();
    String dequeue();
}