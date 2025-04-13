/**
 * Main.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.main;

import java.io.FileInputStream;
import java.io.IOException;

import edu.illinois.abhayp4.brain_project.brain.BrainSimulator;

public class Main {
    public static void main(String[] args) throws IOException {
        BrainSimulator brain = new BrainSimulator(new FileInputStream("application.yml"));
        brain.start(false);
    }
}