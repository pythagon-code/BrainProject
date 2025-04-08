/**
 * OperationalSwitch.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

final class OperationalSwitch extends RootObject {
    
    ModelClient client;

    public OperationalSwitch(String name, ModelClient client) {
        super(name);
        this.client = client;
    }

    public boolean accepts(String input) {
        return Math.random() >= 0.5;
    }
}