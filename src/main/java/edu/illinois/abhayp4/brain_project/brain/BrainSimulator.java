package edu.illinois.abhayp4.brain_project.brain;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
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

import edu.illinois.abhayp4.brain_project.neurons.RelayNeuronFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import edu.illinois.abhayp4.brain_project.neurons.ResponseNeuron;
import edu.illinois.abhayp4.brain_project.workers.ModelWorkerPool;

public final class BrainSimulator {
    public final ModelWorkerPool modelClientService = null;

    public static final Level LOW = new Level("LOW", Level.INFO.intValue() + 1) { };
    public static final Level MEDIUM = new Level("MEDIUM", LOW.intValue() + 1) { };
    public static final Level HIGH = new Level("HIGH", MEDIUM.intValue() + 1) { };

    public BrainSimulator(SimulatorSettings streams) {
        Yaml yaml = new Yaml();
    }
/*
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
 case SimulatorConfigLoader.LogVerbosity.LOW:
 logger.setLevel(LOW);
 break;
 case SimulatorConfigLoader.LogVerbosity.MEDIUM:
 logger.setLevel(MEDIUM);
 break;
 case SimulatorConfigLoader.LogVerbosity.HIGH:
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

 saveCheckpoint(new ResponseNeuron());

 try {
 System.out.println(writer.writeValueAsString(mainConfig));
 }
 catch (IOException e) {
 throw new IOError(e);
 }
 System.out.println("hello");*/

    public void start() {

    }
/*
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
    }*/
}