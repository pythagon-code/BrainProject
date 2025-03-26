/**
 * ModelClient.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.IOError;

import org.json.JSONObject;
import org.json.JSONArray;

public final class ModelClient implements Closeable {
    private static String pythonExecutable, scriptPath;

    public static void initialize(String pythonExec, String script) {
        pythonExecutable = pythonExec;
        scriptPath = System.getProperty("user.dir") + script;
        System.out.println(scriptPath);
    }

    private final Socket socket;
    private final Process process;
    private final BufferedReader serverIn;
    private final PrintWriter serverOut;

    public ModelClient() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();

            ProcessBuilder pb = new ProcessBuilder(pythonExecutable, scriptPath, Integer.toString(port));
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
    }

    public int createModel(String modelClass) throws IOException {
        JSONObject objToSend = new JSONObject();

        objToSend.put("Operation", "CreateModel");
        objToSend.put("ModelClass", modelClass);

        JSONObject received = sendAndReceive(objToSend);
        return received.getInt("ModelID");
    }

    public Object getOutput(int modelID, String outputType, String input) throws IOException {
        JSONObject objToSend = new JSONObject();

        objToSend.put("Operation", "InvokeModel");
        objToSend.put("ModelID", modelID);
        objToSend.put("OutputType", outputType);
        objToSend.put("Input", input);

        return sendAndReceive(objToSend).get("Output");
    }

    public List<Object> getMultipleOutputs(int modelID, String input) throws IOException {
        JSONObject objToSend = new JSONObject();

        objToSend.put("Operation", "InvokeModel");
        objToSend.put("ModelID", modelID);
        objToSend.put("OutputType", "Vector");
        objToSend.put("Input", input);

        JSONArray array = sendAndReceive(objToSend).getJSONArray("OutputArray");
        return array.toList();
    }

    private synchronized JSONObject sendAndReceive(JSONObject obj) throws IOException {
        serverOut.println(obj);
        try {
            return receive();
        }
        catch (IOException e) {
            throw new IOException("Client closed.");
        }
    }

    private synchronized JSONObject receive() throws IOException {
        String line = serverIn.readLine();
        if (line == null) {
            throw new IOException("Client closed.");
        }
        return new JSONObject(line);
    }
    

    public synchronized void close() {
        serverOut.println(JSONObject.NULL);

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

        waitForProccessToExit(7, null);
    }

    private void waitForProccessToExit(int triesLeft, Throwable prevException) {
        if (triesLeft == 0) {
            throw new IOError(prevException);
        }
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Python process exited with error code: " + exitCode + ".");
            }
        }
        catch (InterruptedException e) {
            waitForProccessToExit(triesLeft - 1, e);
        }
    }
}