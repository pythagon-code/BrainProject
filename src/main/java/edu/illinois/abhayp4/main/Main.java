/**
 * Main.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.main;

import edu.illinois.abhayp4.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        
        Application app = new Application();
        app.start();
    }
}