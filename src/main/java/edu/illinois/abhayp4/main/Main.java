/**
 * Main.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.main;

import edu.illinois.abhayp4.Application;

public class Main {
    public static void main(String[] args) {
        Application app = new Application("application.yml");
        app.start(false);
    }
}