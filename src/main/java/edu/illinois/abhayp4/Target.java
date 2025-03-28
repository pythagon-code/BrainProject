/**
 * Target.java
 * @author Abhay
 */

package edu.illinois.abhayp4;

interface Target {
    void setMonitor(Object monitor);
    boolean hasMessage();
    String remove();
}