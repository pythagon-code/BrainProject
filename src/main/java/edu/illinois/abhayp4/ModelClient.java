/**
 * ModelClient.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.IOError;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

final class ModelClient implements Closeable {
    private final Socket socket;
    private final Process process;
    private final BufferedReader serverIn;
    private final PrintWriter serverOut;

    private final List<ModelIdInputPair> inputBatch;
    private final Queue<Object> outputBatch;
    private final ObjectMapper objectMapper;
    private long outputCount = 0;
    private long outputCurrent = 0;

    private final ModelClientInputData startRound;
    private final ModelClientInputData endRound;

    public ModelClient() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();

            ProcessBuilder pb = new ProcessBuilder(
                GlobalState.getPythonExecutable(), GlobalState.getScriptPath(), Integer.toString(port));
            pb.inheritIO();
            process = pb.start();

            socket = serverSocket.accept();
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());

            serverIn = new BufferedReader(isr);
            serverOut = new PrintWriter(socket.getOutputStream(), true);

            serverSocket.close();
        }
        catch (IOException e) {
            throw new IOError(e);
        }

        objectMapper = new ObjectMapper();

        initializeClient();
        
        inputBatch = new ArrayList<>();
        outputBatch = new ArrayDeque<>();

        startRound = new ModelClientInputData("StartRound", -1, null, null);
        endRound = new ModelClientInputData("EndRound", -1, null, null);
    }

    private void initializeClient() {
        Object[] arguments = {
            GlobalState.shouldUseCuda()
        };

        String argumentsJson;
        try {
            argumentsJson = objectMapper.writeValueAsString(arguments);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Error processing JSON", e);
        } 
        
        ModelClientInputData data = new ModelClientInputData(
            "Initialize", -1, argumentsJson, null);
        
        boolean valid = sendAndReceive(data, boolean.class);

        if (!valid) {
            throw new IllegalStateException("Invalid client initialization");
        }

        Thread thread = new Thread(() -> batch(), "ModelClient-BatchThread");
        thread.setDaemon(true);
        thread.start();
    }

    private void batch() {
        while (true) {
            
            try {
                final int DELAY = 10;
                Thread.sleep(DELAY);

            }
            catch (InterruptedException e) {
                GlobalState.waitForResumeSignal();
            }

            synchronized (this) {
                ModelClientInputData data = new ModelClientInputData(
                    "GetBatch", -1, null, inputBatch.toArray(new ModelIdInputPair[inputBatch.size()]));

                Object[] output = sendAndReceive(data, Object[].class);
                outputBatch.addAll(Arrays.asList(output));

                inputBatch.clear();

                notifyAll();
            }
        }
    }

    public void startRound() {
        send(startRound);
    }
    
    public void endRound() {
        send(endRound);
    }

    public int createModel(String modelClass) {
        ModelClientInputData data = new ModelClientInputData(
            "SerializeModel", -1, modelClass, null);
        
        return sendAndReceive(data, int.class);
    }
    
    public void deserializeModel(int modelId) {
        ModelClientInputData data = new ModelClientInputData(
            "DeserializeModel", modelId, null, null);
        
        send(data);
    }

    public String serializeModel(int modelId, String encodedData) {
        ModelClientInputData data = new ModelClientInputData(
            "SerializeModel", modelId, null, null);
        
        return sendAndReceive(data, String.class);
    }

    public Object getOutputBatched(int modelId, String input) {
        ModelIdInputPair pair = new ModelIdInputPair(modelId, input);
        long id = addInput(pair);

        synchronized (this) {
            while (outputCurrent < id) {
                try {
                    wait();
                }
                catch (InterruptedException e) { }
            }

            outputCurrent++;
            return outputBatch.remove();
        }
    }

    public synchronized long addInput(ModelIdInputPair pair) {
        final int MAX_BATCH_SIZE = 16;
        
        while (inputBatch.size() >= MAX_BATCH_SIZE) {
            try {
                wait();
            }
            catch (InterruptedException e) { }
        }

        inputBatch.add(pair);

        return ++outputCount;
    }

    public Object[] getOutput(int modelId, String input) {
        ModelClientInputData data = new ModelClientInputData(
            "InvokeModel", modelId, input, null);

        return sendAndReceive(data, Object[].class);
    }

    private synchronized <R> R sendAndReceive(ModelClientInputData data, Class<R> clazz) {
        send(data);
        try {
            return receive(clazz);
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    private synchronized void send(ModelClientInputData data) {
        try {
            serverOut.println(objectMapper.writeValueAsString(data));
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Error processing JSON", e);
        }
        serverOut.flush();
    }

    private synchronized <R> R receive(Class<R> clazz) throws IOException {
        String line = serverIn.readLine();
        if (line == null) {
            throw new IOException("Client closed unexpectedly");
        }
        return objectMapper.readValue(line, clazz);
    }
    
    @Override
    public synchronized void close() {
        try {
            serverOut.println(objectMapper.writeValueAsString(null));
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Null cannot be converted to JSON", e);
        }
        
        try {
            serverIn.close();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
            serverOut.close();
            try {
                socket.close();
            }
            catch (IOException e2) {
                System.err.println(e2.getMessage());
            }
        }

        waitForProccessToExit();
    }

    private void waitForProccessToExit() {
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Python process exited with error code: " + exitCode + ".");
            }
        }
        catch (InterruptedException e) {
            throw new IllegalThreadStateException("Cannot interrupt during client close");
        }
    }
}

class ModelClientInputData {
    @JsonProperty("Operation") public String operation;
    @JsonProperty("ModelId") public int modelId;
    @JsonProperty("Input") public String input;
    @JsonProperty("Batch") public ModelIdInputPair[] batch;

    public ModelClientInputData(
        String operation, int modelId, String input, ModelIdInputPair[] batch
    ) {
        this.operation = operation;
        this.modelId = modelId;
        this.input = input;
        this.batch = batch;   
    }
}

class ModelIdInputPair {
    @JsonProperty("ModelId") public int modelId;
    @JsonProperty("Input") public String input;

    public ModelIdInputPair(int modelId, String input) {
        this.modelId = modelId;
        this.input = input;
    }
}