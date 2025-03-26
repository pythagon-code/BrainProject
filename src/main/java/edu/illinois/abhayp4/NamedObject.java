/**
 * NamedObject.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

public abstract class NamedObject {
    public String name;

    public NamedObject(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + name;
    }
}