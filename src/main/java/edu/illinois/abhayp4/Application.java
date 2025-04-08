package edu.illinois.abhayp4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

public class Application {
    private final Map<String, Object> config;
    public final MainConfiguration mainConfig;
    public final ModelConfiguration modelConfig;
    public final OptimizationConfiguration optimizationConfig;

    private final ObjectWriter writer;

    public final Logger logger;

    private final String checkpointsFolderPath;

    public static final Level LOW = new Level("LOW", Level.INFO.intValue() + 1) {};
    public static final Level MEDIUM = new Level("MEDIUM", LOW.intValue() + 1) {};
    public static final Level HIGH = new Level("HIGH", MEDIUM.intValue() + 1) {};

    @SuppressWarnings("unchecked")
    public Application(InputStream yamlStream) {
        Yaml yaml = new Yaml();

        Map<String, Object> object = yaml.load(yamlStream);
        config = (Map<String, Object>) object.get("application");

        mainConfig = MainConfiguration.loadFromApplicationConfig(this);
        modelConfig = null;
        optimizationConfig = null;

        ObjectMapper objectMapper = new ObjectMapper();
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        DefaultIndenter indenter = new DefaultIndenter("\t", DefaultIndenter.SYS_LF);
        prettyPrinter.indentObjectsWith(indenter);
        prettyPrinter.indentArraysWith(indenter);

        writer = objectMapper.writer(prettyPrinter);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_z");
        String fileName = mainConfig.logFileNamePrefix + ZonedDateTime.now().format(formatter) + ".log";
        String filePath = Paths.get(mainConfig.logTo, fileName).toString();
        try {
            FileHandler fh = new FileHandler(filePath.replace("%", "%%"), false);
            fh.setFormatter(new SimpleFormatter());

            logger = Logger.getLogger("Application");
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
            File checkpointsFolder = new File(mainConfig.saveCheckpointsTo);
            checkpointsFolder.mkdirs();
            try {
                checkpointsFolderPath = checkpointsFolder.getCanonicalPath();
            }
            catch (IOException e) {
                throw new IOError(e);
            }
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_z");
        String fileName = mainConfig.saveCheckpointsFileNamePrefix + ZonedDateTime.now().format(formatter) + ".json";

        String checkpointsFile = Paths.get(checkpointsFolderPath, fileName).toString();

        try (PrintWriter pw = new PrintWriter(checkpointsFile)) {
            Checkpoint checkpoint = new Checkpoint(mainConfig, optimizationConfig, responseNeuron);
            pw.println(writer.writeValueAsString(checkpoint));
        }
        catch (IOException e) {
            System.err.println("Error saving checkpoint");
            throw new IOError(e);
        }
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

    public class MainConfiguration extends RootObject {
        @JsonProperty("PythonExecutable") public final String pythonExecutable;
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
        @JsonProperty("SaveCheckpointsFrequency") public final long saveCheckpointsFrequency;
        @JsonProperty("LogTo") public final String logTo;
        @JsonProperty("LogFileNamePrefix") public final String logFileNamePrefix;
        @JsonProperty("LogVerbosity") public final LogVerbosity logVerbosity;

        public static enum LogVerbosity {
            LOW,
            MEDIUM,
            HIGH
        };

        @JsonCreator
        public MainConfiguration (
            @JsonProperty("Name") String name,
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
            @JsonProperty("SaveCheckpointsFrequency") int saveCheckpointsFrequency,
            @JsonProperty("LogTo") String logTo,
            @JsonProperty("LogFileNamePrefix") String logFileNamePrefix,
            @JsonProperty("LogVerbosity") LogVerbosity logVerbosity
        ) {
            super(name);
            this.pythonExecutable = pythonExecutable;
            this.nPythonWorkers = nPythonWorkers;
            this.useCuda = useCuda;
            this.cudaDevice = cudaDevice;
            this.trainingAllowed = trainingAllowed;
            this.preloadModelEnabled = preloadModelEnabled;
            this.preloadModelFrom = preloadModelFrom;
            this.errorOnInconsistentModel = errorOnInconsistentModel;
            this.errorOnDifferentOptimization = errorOnDifferentOptimization;
            this.saveCheckpointsEnabled = saveCheckpointsEnabled;
            this.saveCheckpointsTo = saveCheckpointsTo;
            this.saveCheckpointsFileNamePrefix = saveCheckpointsFileNamePrefix;
            this.saveCheckpointsFrequency = saveCheckpointsFrequency;
            this.logTo = logTo;
            this.logFileNamePrefix = logFileNamePrefix;
            this.logVerbosity = logVerbosity;
        }

        public static MainConfiguration loadFromApplicationConfig(Application app) {
            return app.new MainConfiguration(
                app.getNestedField("main_config", "name"),
                app.getNestedField("main_config", "python_executable"),
                app.getNestedField("main_config", "n_python_workers"),
                app.getNestedField("main_config", "use_cuda"),
                app.getNestedField("main_config", "cuda_device"),
                app.getNestedField("main_config", "training_allowed"),
                app.getNestedField("main_config", "preload_model", "enabled"),
                app.getNestedField("main_config", "preload_model", "from"),
                app.getNestedField("main_config", "preload_model", "error_on_inconsistent_model"),
                app.getNestedField("main_config", "preload_model", "error_on_different_optimization"),
                app.getNestedField("main_config", "save_checkpoints", "enabled"),
                app.getNestedField("main_config", "save_checkpoints", "to"),
                app.getNestedFieldOrDefault("", "main_config", "save_checkpoints", "file_name_prefix"),
                app.getNestedField("main_config", "save_checkpoints", "frequency"),
                app.getNestedField("main_config", "log", "to"),
                app.getNestedFieldOrDefault("", "main_config", "log", "file_name_prefix"),
                LogVerbosity.valueOf(((String) app.getNestedField("main_config", "log", "verbosity")).toUpperCase())
            );
        }
    }

    public class ModelConfiguration extends RootObject {
        @JsonCreator
        public ModelConfiguration(@JsonProperty("Name") String name) {
            super(name);
        }
    }

    public class OptimizationConfiguration extends RootObject {
        @JsonCreator
        public OptimizationConfiguration(@JsonProperty("Name") String name) {
            super(name);
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
                throw new NoSuchElementException("Missing field in path " + Arrays.toString(path) + " in application.yml");
            }
            current = ((Map<String, Object>) current).get(field); 
        }
        return (R) current;
    }
}
