/**
 * Target.java
 * @author Abhay
 */

package edu.illinois.abhayp4;

interface Target {
    void setReceiveSignal(Object signal);
    boolean hasMessage();
    String remove();
}