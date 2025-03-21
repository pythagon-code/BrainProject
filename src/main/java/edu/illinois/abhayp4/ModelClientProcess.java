package edu.illinois.abhayp4;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

import org.json.JSONObject;

public class ModelClientProcess {
    private static String pythonExecutable, scriptPath;
    
    public static void initialize(String pythonExec, String script) {
        pythonExecutable = pythonExec;
        scriptPath = System.getProperty("user.dir") + script;
        System.out.println(scriptPath);
    }

    private Socket socket;
    private Process process;
    private BufferedReader serverIn;
    private PrintWriter serverOut;

    public ModelClientProcess() throws IOException {
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

    public void send(JSONObject obj) {
        serverOut.println(obj);
    }

    public boolean hasMessage() throws IOException {
        return serverIn.ready();
    }

    public JSONObject receive() throws IOException {
        if (!hasMessage()) {
            throw new IllegalStateException("No message to receive.");
        }

        return new JSONObject(serverIn.readLine());
    }

    public void close() throws IOException, InterruptedException {
        serverOut.println(JSONObject.NULL);

        socket.close();
        serverIn.close();
        serverOut.close();

        while (true) {
            try {
                process.waitFor();
                break;
            } catch (InterruptedException e) {
                System.err.println("Process was interrupted.");
            }
        }   
    }
}