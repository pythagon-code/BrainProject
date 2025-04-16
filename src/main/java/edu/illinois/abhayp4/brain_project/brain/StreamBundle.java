package edu.illinois.abhayp4.brain_project.brain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record StreamBundle(
    InputStream systemStream,
    InputStream modelArchitectureStream,
    InputStream transformersStream,
    InputStream neuronTopologyStream,
    InputStream baseNeuronStream,
    InputStream graphStructuresStream,
    InputStream optimizationStream
) implements AutoCloseable {
    public StreamBundle(Properties properties) throws IOException {
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

    public StreamBundle(
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
                systemStream,
                modelArchitectureStream,
                transformersStream,
                neuronTopologyStream,
                baseNeuronStream,
                graphStructuresStream,
                optimizationStream
        }) {
            try {
                if (stream != null) stream.close();
            } catch (IOException e) {
                if (exception == null) exception = e;
                else exception.addSuppressed(e);
            }
        }

        if (exception != null) {
            throw exception;
        }
    }
}