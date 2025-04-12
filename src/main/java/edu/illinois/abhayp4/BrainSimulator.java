package edu.illinois.abhayp4;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import edu.illinois.abhayp4.client.ModelClientPool;

public final class BrainSimulator {
    private final Map<String, Object> config;
    public final MainConfiguration mainConfig;
    public final ModelConfiguration modelConfig;
    public final OptimizationConfiguration optimizationConfig;
    
    public final ModelClientPool modelClientService = null;

    private final ObjectWriter writer;

    public final Logger logger;

    private final String checkpointsFolderPath;

    public static final Level LOW = new Level("LOW", Level.INFO.intValue() + 1) {};
    public static final Level MEDIUM = new Level("MEDIUM", LOW.intValue() + 1) {};
    public static final Level HIGH = new Level("HIGH", MEDIUM.intValue() + 1) {};

    @SuppressWarnings("unchecked")
    public BrainSimulator(InputStream yamlStream) {
        Yaml yaml = new Yaml();

        Map<String, Object> object = yaml.load(yamlStream);
        config = (Map<String, Object>) object.get("application");

        mainConfig = MainConfiguration.loadFromApplicationConfiguration(this);
        modelConfig = null;
        optimizationConfig = null;

        ObjectMapper objectMapper = new ObjectMapper();
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        DefaultIndenter indenter = new DefaultIndenter("\t", DefaultIndenter.SYS_LF);
        prettyPrinter.indentObjectsWith(indenter);
        prettyPrinter.indentArraysWith(indenter);

        writer = objectMapper.writer(prettyPrinter);
        
        String timestamp = getTimestamp();
        
        String logFolderPath = Paths.get(
            mainConfig.logTo.replace("%", "%%"), timestamp).toString();
        new File(logFolderPath).mkdirs();

        String logFileName = mainConfig.logFileNamePrefix.replace("%", "%%") + "%g.log";
        String logFilePath = Paths.get(logFolderPath, logFileName).toString();

        try {
            final int BYTES_PER_MB = 1024 * 1024;
            FileHandler fh = new FileHandler(
                logFilePath, BYTES_PER_MB * mainConfig.maxSizePerLog, mainConfig.nRotatingLogs, true);
            fh.setFormatter(new SimpleFormatter());

            logger = Logger.getLogger("brain-simulation");
            logger.addHandler(fh);
        }
        catch (IOException e) {
            throw new IOError(e);
        }

        switch (mainConfig.logVerbosity) {
        case MainConfiguration.LogVerbosity.LOW:
            logger.setLevel(LOW);
            break;
        case MainConfiguration.LogVerbosity.MEDIUM:
            logger.setLevel(MEDIUM);
            break;
        case MainConfiguration.LogVerbosity.HIGH:
            logger.setLevel(HIGH);
        }

        logger.addHandler(new ConsoleHandler());
        logger.setUseParentHandlers(true);

        logger.log(LOW, "hello");

        if (mainConfig.saveCheckpointsEnabled) {
            checkpointsFolderPath = Paths.get(mainConfig.saveCheckpointsTo, timestamp).toString();
            File checkpointsFolder = new File(checkpointsFolderPath);
            checkpointsFolder.mkdirs();
        }
        else {
            checkpointsFolderPath = null;
        }

        saveCheckpoint(new ResponseNeuron("hi"));

        try {
            System.out.println(writer.writeValueAsString(mainConfig));
        }
        catch (IOException e) {
            throw new IOError(e);
        }
        System.out.println("hello");
    }

    public void start(boolean test) {

    }

    private void saveCheckpoint(ResponseNeuron responseNeuron) {
        if (!mainConfig.saveCheckpointsEnabled) {
            return;
        }

        String fileName = mainConfig.saveCheckpointsFileNamePrefix + getTimestamp() + ".json";
        String checkpointsFilePath = Paths.get(checkpointsFolderPath, fileName).toString();

        try (PrintWriter pw = new PrintWriter(checkpointsFilePath)) {
            Checkpoint checkpoint = new Checkpoint(mainConfig, optimizationConfig, responseNeuron);
            pw.println(writer.writeValueAsString(checkpoint));
        }
        catch (IOException e) {
            System.err.println("Error saving checkpoint");
            throw new IOError(e);
        }
    }

    private String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_z");
        return ZonedDateTime.now().format(formatter);
    }

    private class Checkpoint {
        @JsonProperty("ModelConfig") public final MainConfiguration modelConfig;
        @JsonProperty("OptimizationConfig") public final OptimizationConfiguration optimizationConfig;
        @JsonProperty("ResponseNeuron") public final ResponseNeuron responseNeuron;

        @JsonCreator
        public Checkpoint(
            @JsonProperty("ModelConfig") MainConfiguration modelConfig,
            @JsonProperty("OptimizationConfig") OptimizationConfiguration optimizationConfig,
            @JsonProperty("ResponseNeuron") ResponseNeuron responseNeuron
        ) {
            this.modelConfig = modelConfig;
            this.optimizationConfig = optimizationConfig;
            this.responseNeuron = responseNeuron;
        }
    }

    public record MainConfiguration(
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
