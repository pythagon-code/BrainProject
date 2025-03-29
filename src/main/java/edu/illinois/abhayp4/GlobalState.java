/**
 * GlobalState.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.util.ArrayDeque;
import java.util.Queue;

import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.IOError;

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
    private static int logVerbosity;
    private static int nLevels;

    private static int nThreads;
    private static volatile LocalDateTime timestamp;

    private static PrintWriter logFile;

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
        logVerbosity = Integer.parseInt(args[i++]);
        nLevels = Integer.parseInt(args[i++]);

        nThreads = 0;
        final int SPLIT_FACTOR = 4;
        for (int j = 1; j <= nLevels; j++) {
            nThreads += Math.pow(j, SPLIT_FACTOR);
        }

        stampTime();
        String logFileName = logFileNamePrefix + " " + getTimestampNoColons() + ".log";
        String logFilePath = Paths.get(logTo, logFileName).toString();
        if (logEnabled) {
            try {
                logFile = new PrintWriter(logFilePath);
            }
            catch (FileNotFoundException e) {
                logEnabled = false;
            }
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

    static int getLogVerbosity() {
        return logVerbosity;
    }

    static int getNLevels() {
        return nLevels;
    }

    static int getNThreads() {
        return nThreads;
    }

    static void log(int verbosity, String logMessage) {
        if (logEnabled && verbosity <= logVerbosity) {
            logFile.println(logMessage);
        }
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