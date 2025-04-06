/**
 * Target.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

interface Target {
    void setReceiveSignal(Object signal);
    boolean hasMessage();
    String dequeue();
}