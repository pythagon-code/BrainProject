package edu.illinois.abhayp4;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.IOError;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

import org.yaml.snakeyaml.Yaml;

public class Application {
    private static Application app = null;

    private final Map<String, Object> config;
    final MainConfiguration mainConfig;
    final ModelConfiguration modelConfig;
    final OptimizationConfiguration optimizationConfig;

    @SuppressWarnings("unchecked")
    private Application() {
        try (InputStream stream = new FileInputStream("application.yml")) {
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

    public static void start() {
        if (app != null) {
            throw new IllegalStateException("Application already started");
        }
        
        app = new Application();
    }

    static class MainConfiguration {
        
    }

    static class ModelConfiguration {

    }

    static class OptimizationConfiguration {

    }
}