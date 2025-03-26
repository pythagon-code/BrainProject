/**
 * Removable.java
 * @author Abhay
 */

package edu.illinois.abhayp4;

public interface Removable {
    void setMonitor(Object monitor);
    boolean hasMessage();
    String remove();
}