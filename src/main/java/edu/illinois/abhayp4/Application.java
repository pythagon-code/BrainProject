package edu.illinois.abhayp4;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.IOError;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Application {
    private final Map<String, Object> config;
    private final MainConfiguration mainConfig;
    private final ModelConfiguration modelConfig;
    private final OptimizationConfiguration optimizationConfig;

    @SuppressWarnings("unchecked")
    public Application(String yamlFile) {
        try (InputStream stream = new FileInputStream(yamlFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> object = yaml.load(stream);
            config = (Map<String, Object>) object.get("application");
            mainConfig = null;
            modelConfig = null;
            optimizationConfig = null;

            System.out.println("hello");
        }
        catch (IOException e) {
            throw new IOError(e);
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
                throw new NoSuchElementException("Field " + Arrays.toString(path) + " does not exist in application.yml");
            }
            current = ((Map<String, Object>) current).get(field); 
        }
        return (R) current;
    }

    public void start(boolean test) {
        
    }

    class MainConfiguration {
        @JsonProperty("PythonExecutable") public final int pythonExecutable;
        @JsonProperty("NPythonWorkers") public final int nPythonWorkers;
        @JsonProperty("UseCuda") public final boolean useCuda;
        @JsonProperty("CudaDevice") public final int cudaDevice;
        @JsonProperty("TrainingAllowed") public final boolean trainingAllowed;
        @JsonProperty("PreloadModelEnabled") public final boolean preloadModelEnabled;
        @JsonProperty("PreloadModelFrom") public final String preloadModelFrom;
        @JsonProperty("ErrorOnInconsistentModel") public final boolean errorOnInconsistentModel;
        @JsonProperty("ErrorOnDifferentOptimization") public final boolean errorOnDifferentOptimization;
        @JsonProperty("SaveCheckpointsEnabled") public final boolean saveCheckpointsEnabled;
        @JsonProperty("SaveCheckpointsTo") public final String saveCheckpointsTo;
        @JsonProperty("SaveCheckpointsFileNamePrefix") public final String saveCheckpointsFileNamePrefix;
        @JsonProperty("SaveCheckpointsFrequency") public final int saveCheckpointsFrequency;
        @JsonProperty("LogEnabled") public final boolean logEnabled;
        @JsonProperty("LogTo") public final String logTo;
        @JsonProperty("LogFileNamePrefix") public final String logFileNamePrefix;
        @JsonProperty("LogVerbosity") public final LogVerbosity logVerbosity;

        public enum LogVerbosity {
            LOW,
            MEDIUM,
            HIGH
        };

        public MainConfiguration() {
            pythonExecutable = getNestedField("main_config", "python_executable");
            nPythonWorkers = getNestedField("main_config", "n_python_workers");
            useCuda = getNestedField("main_config", "use_cuda");
            cudaDevice = getNestedField("main_config", "cuda_device");
            trainingAllowed = getNestedField("main_config", "training_allowed");
            preloadModelEnabled = getNestedField("main_config", "preload_model", "enabled");
            preloadModelFrom = getNestedField("main_config", "preload_model", "from");
            errorOnInconsistentModel = getNestedField("main_config", "preload_model", "error_on_inconsistent_model");
            errorOnDifferentOptimization = getNestedField("main_config", "preload_model", "error_on_different_optimization");
            saveCheckpointsEnabled = getNestedField("main_config", "save_checkpoints", "enabled");
            saveCheckpointsTo = getNestedField("main_config", "save_checkpoints", "to");
            saveCheckpointsFileNamePrefix = getNestedFieldOrDefault("", "main_config", "save_checkpoints", "file_name_prefix");
            saveCheckpointsFrequency = getNestedField("main_config", "save_checkpoints", "frequency");
            logEnabled = getNestedField("main_config", "log", "enabled");
            logTo = getNestedField("main_config", "log", "to");
            logFileNamePrefix = getNestedFieldOrDefault("", "main_config", "log", "file_name_prefix");
            String logVerbosityStr = getNestedField("main_config", "log", "verbosity");
            logVerbosity = LogVerbosity.valueOf(logVerbosityStr);
        }
    }

    class ModelConfiguration {

    }

    class OptimizationConfiguration {
        
    }
}