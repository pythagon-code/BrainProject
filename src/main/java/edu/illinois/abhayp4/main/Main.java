/**
 * Main.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.main;

import java.io.FileInputStream;
import java.io.IOException;

import edu.illinois.abhayp4.Application;

public class Main {
    public static void main(String[] args) throws IOException {
        Application app = new Application(new FileInputStream("application.yml"));
        app.start(false);
    }
}