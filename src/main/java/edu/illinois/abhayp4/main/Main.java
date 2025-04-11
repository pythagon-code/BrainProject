/**
 * Main.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.main;

import java.io.FileInputStream;
import java.io.IOException;

import edu.illinois.abhayp4.BrainSimulator;

public class Main {
    public static void main(String[] args) throws IOException {
        BrainSimulator sim = new BrainSimulator(new FileInputStream("application.yml"));
        sim.start(false);
    }
}