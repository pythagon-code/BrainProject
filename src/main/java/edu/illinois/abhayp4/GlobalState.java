/**
 * GlobalBehavior.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

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
    private static String logFileNameVerbosity;
    private static int nLevels;

    private GlobalState() { }

    public static void initialize(String[] args) {
        if (initialized) {
            throw new IllegalStateException("GlobalBehavior is already initialized.");
        }
    
        if (args == null || args.length < 15) {
            throw new IllegalArgumentException("Insufficient arguments to initialize GlobalBehavior.");
        }
    
        initialized = true;

        scriptPath = args[0];
        pythonExecutable = args[1];
        nPythonWorkers = Integer.parseInt(args[2]);
        useCuda = Boolean.parseBoolean(args[3]);
        trainingMode = Boolean.parseBoolean(args[4]);
        preloadModelEnabled = Boolean.parseBoolean(args[5]);
        preloadModelFrom = args[6];
        errorOnInconsistentPreload = Boolean.parseBoolean(args[7]);
        saveModelEnabled = Boolean.parseBoolean(args[8]);
        saveModelTo = args[9];
        saveModelFileNamePrefix = args[10];
        saveModelFrequency = Integer.parseInt(args[11]);
        logEnabled = Boolean.parseBoolean(args[12]);
        logTo = args[13];
        logFileNameVerbosity = args[14];
        nLevels = Integer.parseInt(args[15]);
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

    static String getLogFileNameVerbosity() {
        return logFileNameVerbosity;
    }

    static int getNLevels() {
        return nLevels;
    }
}