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

import java.util.concurrent.CompletableFuture;

import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.IOError;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectMapper;

final class ModelClient implements Closeable {
    private final Socket socket;
    private final Process process;
    private final BufferedReader serverIn;
    private final PrintWriter serverOut;

    private List<ModelIdInputPair> inputBatch;
    private Queue<Object> outputBatch;
    private ObjectMapper objectMapper;
    private volatile long outputCount = 0;
    private volatile long outputCurrent = 0;

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

        initializeClient();

        startRound = new ModelClientInputData("StartRound", -1, null, null);
        endRound = new ModelClientInputData("EndRound", -1, null, null);
    }

    private void initializeClient() {
        Object[] arguments = {
            GlobalState.shouldUseCuda()
        };

        objectMapper = new ObjectMapper();
        
        String argumentsJson = objectMapper.writeValueAsString(arguments);
        
        ModelClientInputData data = new ModelClientInputData(
            "Initialize", -1, argumentsJson, null);
        
        boolean valid = sendAndReceive(data, boolean.class);

        if (!valid) {
            throw new IllegalStateException("Invalid client intialization");
        }

        Thread thread = new Thread(() -> batch());
        thread.setDaemon(true);
        thread.start();

        
        inputBatch = new ArrayList<>();
        outputBatch = new ArrayDeque<>();
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
                    "GetBatch", -1, null, null);

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
        addInput(pair);

        long id = ++outputCount;

        synchronized (this) {
            while (outputCurrent < id) {
                try {
                    wait();
                }
                catch (InterruptedException e) { }
            }

            return outputBatch.remove();
        }
    }

    public synchronized void addInput(ModelIdInputPair pair) {
        final int MAX_BATCH_SIZE = 16;
        
        while (inputBatch.size() >= MAX_BATCH_SIZE) {
            try {
                wait();
            }
            catch (InterruptedException e) { }
        }

        inputBatch.add(pair);
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
        serverOut.println(objectMapper.writeValueAsString(data));
        serverOut.flush();
    }

    private synchronized <R> R receive(Class<R> clazz) throws IOException {
        String line = serverIn.readLine();
        if (line == null) {
            throw new IOException("Client closed");
        }
        return objectMapper.readValue(line, clazz);
    }
    
    @Override
    public synchronized void close() {
        serverOut.println(objectMapper.writeValueAsString(null));

        try {
            socket.close();
        }
        catch (IOException e) { }
        finally {
            try {
                serverIn.close();
            }
            catch (IOException e2) { }
            finally {
                serverOut.close();
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
    @JsonProperty("Operation")
    public String operation;

    @JSONProperty("ModelId")
    public int modelId;

    @JSONProperty("Input")
    public String input;
    
    @JSONProperty("Batch")
    public ModelIdInputPair[] batch;

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
    public int modelId;
    public String input;

    public ModelIdInputPair(int modelId, String input) {
        this.modelId = modelId;
        this.input = input;
    }
}