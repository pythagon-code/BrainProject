package edu.illinois.abhayp4.brain_project.main;

import edu.illinois.abhayp4.brain_project.brain.BrainSimulator;
import edu.illinois.abhayp4.brain_project.brain.SimulatorSettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("simulator.properties")) {
            Properties properties = new Properties();
            properties.load(stream);

            SimulatorSettings settings = new SimulatorSettings(properties);

            BrainSimulator brain = new BrainSimulator(settings);
            brain.start();
        }
    }
}