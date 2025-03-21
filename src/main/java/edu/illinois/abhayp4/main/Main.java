package edu.illinois.abhayp4.main;

import java.io.IOException;

import org.json.JSONObject;

import edu.illinois.abhayp4.*;

public class Main {
    public static void main(String[] args) throws IOException {
        ModelClientProcess.initialize(args[0], args[1]);

        System.out.println("hello world");
        
        for (int i = 0; i < 100; i++) {
            ModelClientProcess model = new ModelClientProcess();
            model.send(new JSONObject("{\"strin\":null}"));
        }
        
        System.out.println("hello world");

        while (true) {

        }
    }
}