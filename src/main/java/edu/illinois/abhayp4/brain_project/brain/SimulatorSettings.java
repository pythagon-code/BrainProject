package edu.illinois.abhayp4.brain_project.brain;

import org.yaml.snakeyaml.Yaml;

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
) {
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
            new Yaml(),
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
            Yaml yaml,
            InputStream systemStream,
            InputStream modelArchitectureStream,
            InputStream transformersStream,
            InputStream neuronTopologyStream,
            InputStream baseNeuronStream,
            InputStream graphStructuresStream,
            InputStream optimizationStream
    ) {
        this(
            yaml.<Map<String, Object>>load(systemStream),
            yaml.<Map<String, Object>>load(modelArchitectureStream),
            yaml.<Map<String, Object>>load(transformersStream),
            yaml.<Map<String, Object>>load(neuronTopologyStream),
            yaml.<Map<String, Object>>load(baseNeuronStream),
            yaml.<Map<String, Object>>load(graphStructuresStream),
            yaml.<Map<String, Object>>load(optimizationStream)
        );
    }
}