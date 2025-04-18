package edu.illinois.abhayp4.brain_project.brain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public record SimulatorSettings(
    Map<String, Object> systemObject,
    Map<String, Object> modelArchitectureObject,
    Map<String, Object> transformersObject,
    Map<String, Object> neuronTopologyObject,
    Map<String, Object> baseNeuronObject,
    Map<String, Object> graphStructuresObject,
    Map<String, Object> optimizationObject
) implements AutoCloseable {
    public SimulatorSettings(Properties properties) throws IOException {
        this(
            properties.getProperty("system"),
            properties.getProperty("modelArchitecture"),
            properties.getProperty("transformers"),
            properties.getProperty("neuronTopology"),
            properties.getProperty("baseNeuron"),
            properties.getProperty("graphStructures"),
            properties.getProperty("optimization")
        );
    }

    public SimulatorSettings(
            String systemFile,
            String modelArchitectureFile,
            String transformersFile,
            String neuronTopologyFile,
            String baseNeuronFile,
            String graphStructuresFile,
            String optimizationFile
    ) throws IOException {
        this(
            new FileInputStream(systemFile),
            new FileInputStream(modelArchitectureFile),
            new FileInputStream(transformersFile),
            new FileInputStream(neuronTopologyFile),
            new FileInputStream(baseNeuronFile),
            new FileInputStream(graphStructuresFile),
            new FileInputStream(optimizationFile)
        );
    }

    public SimulatorSettings(
            String systemFile,
            String modelArchitectureFile,
            String transformersFile,
            String neuronTopologyFile,
            String baseNeuronFile,
            String graphStructuresFile,
            String optimizationFile
    ) throws IOException {
        this(
                new FileInputStream(systemFile),
                new FileInputStream(modelArchitectureFile),
                new FileInputStream(transformersFile),
                new FileInputStream(neuronTopologyFile),
                new FileInputStream(baseNeuronFile),
                new FileInputStream(graphStructuresFile),
                new FileInputStream(optimizationFile)
        );
    }

    @Override
    public void close() throws IOException {
        IOException exception = null;

        for (InputStream stream : new InputStream[] {
                systemObject,
                modelArchitectureObject,
                transformers,
                neuronTopologyStream,
                baseNeuronStream,
                graphStructuresStream,
                optimizationStream
        }) {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (IOException e) {
                if (exception == null) {
                    exception = e;
                }
                else {
                    exception.addSuppressed(e);
                }
            }
        }

        if (exception != null) {
            throw exception;
        }
    }
}