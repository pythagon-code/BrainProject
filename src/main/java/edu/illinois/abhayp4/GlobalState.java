/**
 * GlobalState.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.nio.file.Paths;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.IOError;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

public final class GlobalState {
    private static boolean initialized = false;
    
    private static String scriptPath;
    private static String pythonExecutable;
    private static int nPythonWorkers;
    private static boolean useCuda;
    private static boolean trainingMode;
    private static boolean preloadModelEnabled;
    private static String preloadModelFrom;
    private static boolean errorOnInconsistentPreload;
    private static boolean saveModelEnabled;
    private static String saveModelTo;
    private static String saveModelFileNamePrefix;
    private static int saveModelFrequency;
    private static boolean logEnabled;
    private static String logTo;
    private static String logFileNamePrefix;
    private static LogVerbosity logVerbosity;
    private static int nLevels;

    private static int nThreads;
    private static volatile ZonedDateTime timestamp;
    private static volatile boolean resumed = false;
    private static final Object resumeSignal = new Object();
    private static String checkpointsFolderName;

    private static PrintWriter logFile;

    enum LogVerbosity{
        LOW,
        MEDIUM,
        HIGH;

        public static LogVerbosity get(int level) {
            switch (level) {
                case 1:
                    return LOW;
                case 2:
                    return MEDIUM;
                case 3:
                    return HIGH;
                default:
                    throw new IllegalArgumentException("LogVerbosity must be from 1 to 3.");
            }
        }
    };

    private GlobalState() { }

    public static void initialize(String[] args) {
        if (initialized) {
            throw new IllegalStateException("GlobalBehavior is already initialized.");
        }

        final int N_ARGS = 17;
        if (args == null || args.length != N_ARGS) {
            throw new IllegalArgumentException("Insufficient arguments to initialize GlobalBehavior.");
        }
    
        initialized = true;

        int i = 0;
        scriptPath = args[i++];
        pythonExecutable = args[i++];
        nPythonWorkers = Integer.parseInt(args[i++]);
        useCuda = Boolean.parseBoolean(args[i++]);
        trainingMode = Boolean.parseBoolean(args[i++]);
        preloadModelEnabled = Boolean.parseBoolean(args[i++]);
        preloadModelFrom = args[i++];
        errorOnInconsistentPreload = Boolean.parseBoolean(args[i++]);
        saveModelEnabled = Boolean.parseBoolean(args[i++]);
        saveModelTo = args[i++];
        saveModelFileNamePrefix = args[i++];
        saveModelFrequency = Integer.parseInt(args[i++]);
        logEnabled = Boolean.parseBoolean(args[i++]);
        logTo = args[i++];
        logFileNamePrefix = args[i++];
        logVerbosity = LogVerbosity.get(Integer.parseInt(args[i++]));
        
        if (!preloadModelEnabled) {
            nLevels = Integer.parseInt(args[i++]);
        }
        else {
            JSONObject checkpoint = getPreloadCheckpoint();
            System.out.println(checkpoint.toString(4));
            nLevels = checkpoint.getJSONObject("Model").getInt("NLevels");
        }

        nThreads = 0;
        final int SPLIT_FACTOR = 4;
        for (int j = 1; j <= nLevels; j++) {
            nThreads += Math.pow(j, SPLIT_FACTOR);
        }

        stampTime();
        if (logEnabled) {
            String logFileName = logFileNamePrefix + getTimestampNoColons() + ".log";
            String logFilePath = Paths.get(logTo, logFileName).toString();
            System.out.println("Log file path: " + logFilePath);
            try {
                logFile = new PrintWriter(logFilePath);
            }
            catch (FileNotFoundException e) {
                logEnabled = false;
                System.err.println("Your log file path is incorrect. Please modify it in application.yml.");
            }
        }

        if (saveModelEnabled) {
            checkpointsFolderName = Paths.get(saveModelTo, getTimestampNoColons()).toString();
            File checkpointsFolder = new File(checkpointsFolderName);
            checkpointsFolder.mkdirs();
            System.out.println("Checkpoints folder path:" + checkpointsFolderName);
        }

        // Test
        try {
            Thread.sleep(1000);
            } catch (Exception e) {}
        makeCheckpoint(new JSONObject("{ \"Test\": \"Test\" }"));
    }

    public static String getScriptPath() {
        return scriptPath;
    }

    public static String getPythonExecutable() {
        return pythonExecutable;
    }

    public static int getNPythonWorkers() {
        return nPythonWorkers;
    }

    public static boolean shouldUseCuda() {
        return useCuda;
    }

    public static boolean isTrainingMode() {
        return trainingMode;
    }

    public static boolean isPreloadModelEnabled() {
        return preloadModelEnabled;
    }

    public static String getPreloadModelFrom() {
        return preloadModelFrom;
    }

    public static boolean shouldErrorOnInconsistentPreload() {
        return errorOnInconsistentPreload;
    }

    public static boolean isSaveModelEnabled() {
        return saveModelEnabled;
    }

    public static String getSaveModelTo() {
        return saveModelTo;
    }

    public static String getSaveModelFileNamePrefix() {
        return saveModelFileNamePrefix;
    }

    public static int getSaveModelFrequency() {
        return saveModelFrequency;
    }

    public static boolean isLogEnabled() {
        return logEnabled;
    }

    public static String getLogTo() {
        return logTo;
    }

    public static String getLogFileNamePrefix() {
        return logFileNamePrefix;
    }

    public static LogVerbosity getLogVerbosity() {
        return logVerbosity;
    }

    public static int getNLevels() {
        return nLevels;
    }

    public static int getNThreads() {
        return nThreads;
    }

    public static void log(LogVerbosity verbosity, String logMessage) {
        if (logEnabled && verbosity.compareTo(logVerbosity) >= 0) {
            logFile.println(logMessage);
            logFile.flush();
        }
    }

    public static void stampTime() {
        timestamp = ZonedDateTime.now();
    }

    public static String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return timestamp.format(formatter);
    }

    public static String getTimestampNoColons() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss z");
        return timestamp.format(formatter);
    }

    public static void waitForResumeSignal() {
        synchronized (resumeSignal) {
            while (!resumed) {
                try {
                    resumeSignal.wait();
                }
                catch (InterruptedException e) {
                    throw new IllegalThreadStateException("Cannot be interrupted while waiting for resume signal.");
                }
            }
        }
    }

    public static void resume() {
        synchronized (resumeSignal) {
            resumed = true;
            resumeSignal.notifyAll();
        }
    }

    public static void makeCheckpoint(JSONObject data) {
        stampTime();
        
        if (!saveModelEnabled) {
            throw new IllegalStateException("Cannot make checkpoint as save model not enabled.");
        }

        JSONObject checkpoint = new JSONObject();
        checkpoint.put("Timestamp", getTimestamp());
        
        JSONObject modelParameters = new JSONObject();
        modelParameters.put("NLevels", nLevels);
        
        checkpoint.put("Model", modelParameters);
        checkpoint.put("Data", data);

        String checkpointFileName = saveModelFileNamePrefix + getTimestampNoColons() + ".json";
        String checkpointFilePath = Paths.get(checkpointsFolderName, checkpointFileName).toString();
        
        try (PrintWriter writer = new PrintWriter(checkpointFilePath)) {
            final int INDENT_SIZE = 4;
            writer.println(checkpoint.toString(INDENT_SIZE));
            log(LogVerbosity.HIGH, "Checkpoint saved to: " + checkpointFilePath.replace('\\', '/'));
            
        }
        catch (FileNotFoundException e) {
            throw new IllegalStateException("Checkpoint folder was illegally deleted.");
        }
    }

    public static JSONObject getPreloadCheckpoint() {
        if (!preloadModelEnabled) {
            throw new IllegalStateException("Cannot preload checkpoint as preload model is not enabled.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(preloadModelFrom))) {
            StringBuilder jsonString = new StringBuilder();

            while (reader.ready()) {
                jsonString.append(reader.readLine() + "\n");
            }

            return new JSONObject(jsonString.toString());
        }
        catch (FileNotFoundException e) {
            throw new IllegalStateException("Preload file path is incorrect. Please modify it in application.yml.");
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }
}