/**
 * GlobalState.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private static String logVerbosity;
    private static int nLevels;

    private static int nThreads = 0;
    private static LocalDateTime timestamp = LocalDateTime.now();

    private GlobalState() { }

    public static void initialize(String[] args) {
        if (initialized) {
            throw new IllegalStateException("GlobalBehavior is already initialized.");
        }
    
        if (args == null || args.length != 17) {
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
        logVerbosity = args[i++];
        nLevels = Integer.parseInt(args[i++]);

        for (int j = 1; j <= nLevels; j++) {
            nThreads += Math.pow(j, 4);
        }
    }

    static String getScriptPath() {
        return scriptPath;
    }

    static String getPythonExecutable() {
        return pythonExecutable;
    }

    static int getNPythonWorkers() {
        return nPythonWorkers;
    }

    static boolean shouldUseCuda() {
        return useCuda;
    }

    static boolean isTrainingMode() {
        return trainingMode;
    }

    static boolean isPreloadModelEnabled() {
        return preloadModelEnabled;
    }

    static String getPreloadModelFrom() {
        return preloadModelFrom;
    }

    static boolean shouldErrorOnInconsistentPreload() {
        return errorOnInconsistentPreload;
    }

    static boolean isSaveModelEnabled() {
        return saveModelEnabled;
    }

    static String getSaveModelTo() {
        return saveModelTo;
    }

    static String getSaveModelFileNamePrefix() {
        return saveModelFileNamePrefix;
    }

    static int getSaveModelFrequency() {
        return saveModelFrequency;
    }

    static boolean isLogEnabled() {
        return logEnabled;
    }

    static String getLogTo() {
        return logTo;
    }

    static String getLogFileNamePrefix() {
        return logFileNamePrefix;
    }

    static String getLogVerbosity() {
        return logVerbosity;
    }

    static int getNLevels() {
        return nLevels;
    }

    static int getNThreads() {
        return nThreads;
    }

    static void stampTime() {
        timestamp = LocalDateTime.now();
    }

    static String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss z");
        return timestamp.format(formatter);
    }

    static String getTimestampNoColons() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH_mm_ss z");
        return timestamp.format(formatter);
    }
}