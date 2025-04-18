package edu.illinois.abhayp4.brain_project.brain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class SimulatorConfigLoader {
    private record Checkpoint {
        @JsonProperty("SystemConfig") systemConfig systemConfig,
        @JsonProperty("ModelArchitectureConfig") modelArchitectureConfig modelArchitectureConfig,
        @JsonProperty("TransformersConfig") transformersConfig transformersConfig,
        @JsonProperty("NeuronTopologyConfig") neuronTopologyConfig neuronTopologyConfig,
        @JsonProperty("BaseNeuronConfig") baseNeuronConfig baseNeuronConfig,
        @JsonProperty("GraphStructuresConfig") graphStructuresConfig graphStructuresConfig,
        @JsonProperty("OptimizationConfig") optimizationConfig optimizationConfig,
        @JsonProperty("RelayNeuronFactory") neuronFactory relayNeuronFactory
    }

    public record SystemConfig(
        @JsonProperty("PythonExecutable") String pythonExecutable,
        @JsonProperty("NPythonWorkers") int nPythonWorkers,
        @JsonProperty("UseCuda") boolean useCuda,
        @JsonProperty("CudaDevice") int cudaDevice,
        @JsonProperty("TrainingAllowed") boolean trainingAllowed,
        @JsonProperty("PreloadModelEnabled") boolean preloadModelEnabled,
        @JsonProperty("PreloadModelFrom") String preloadModelFrom,
        @JsonProperty("ErrorOnInconsistentModel") boolean errorOnInconsistentModel,
        @JsonProperty("ErrorOnDifferentOptimization") boolean errorOnDifferentOptimization,
        @JsonProperty("SaveCheckpointsEnabled") boolean saveCheckpointsEnabled,
        @JsonProperty("SaveCheckpointsTo") String saveCheckpointsTo,
        @JsonProperty("SaveCheckpointsFileNamePrefix") String saveCheckpointsFileNamePrefix,
        @JsonProperty("SaveCheckpointsFrequency") long saveCheckpointsFrequency,
        @JsonProperty("LogTo") String logTo,
        @JsonProperty("LogFileNamePrefix") String logFileNamePrefix,
        @JsonProperty("NRotatingLogs") int nRotatingLogs,
        @JsonProperty("MaxSizePerLog") int maxSizePerLog,
        @JsonProperty("LogVerbosity") LogVerbosity logVerbosity
    ) {

        public static enum LogVerbosity {
            LOW,
            MEDIUM,
            HIGH
        };

        public static MainConfiguration loadFromApplicationConfiguration(BrainSimulator sim) {
            return new MainConfiguration(
                sim.getNestedField("main_config", "python_executable"),
                sim.getNestedField("main_config", "n_python_workers"),
                sim.getNestedField("main_config", "use_cuda"),
                sim.getNestedField("main_config", "cuda_device"),
                sim.getNestedField("main_config", "training_allowed"),
                sim.getNestedField("main_config", "preload_model", "enabled"),
                sim.getNestedField("main_config", "preload_model", "from"),
                sim.getNestedField("main_config", "preload_model", "error_on_inconsistent_model"),
                sim.getNestedField("main_config", "preload_model", "error_on_different_optimization"),
                sim.getNestedField("main_config", "save_checkpoints", "enabled"),
                sim.getNestedField("main_config", "save_checkpoints", "to"),
                sim.getNestedFieldOrDefault("", "main_config", "save_checkpoints", "file_name_prefix"),
                sim.getNestedField("main_config", "save_checkpoints", "frequency"),
                sim.getNestedField("main_config", "log", "to"),
                sim.getNestedFieldOrDefault("", "main_config", "log", "file_name_prefix"),
                sim.getNestedField("main_config", "log", "n_rotating_logs"),
                sim.getNestedField("main_config", "log", "max_size_per_log"),
                LogVerbosity.valueOf(((String) sim.getNestedField("main_config", "log", "verbosity")).toUpperCase())
            );
        }
    }

    public class ModelConfiguration {
        @JsonCreator
        public ModelConfiguration(@JsonProperty("Name") String name) {
        }
    }

    public class OptimizationConfiguration {
        @JsonCreator
        public OptimizationConfiguration() {

        }
    }

    @SuppressWarnings("unchecked")
    private <R> R getNestedFieldOrDefault(R defaultValue, String... path) {
        Object result = getNestedField(path);
        if (result == null) {
            return defaultValue;
        }
        else {
            return (R) result;
        }
    }

    @SuppressWarnings("unchecked")
    private <R> R getNestedField(String... path) {
        Object current = config;
        for (String field : path) {
            if (current == null || !(current instanceof Map)) {
                throw new NoSuchElementException("Missing field in path " + Arrays.toString(path) + " in configuration");
            }
            current = ((Map<String, Object>) current).get(field);
        }
        return (R) current;
    }
}
