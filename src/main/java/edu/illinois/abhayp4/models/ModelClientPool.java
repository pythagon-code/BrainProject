/**
 * ModelClientService.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.models;

import java.io.Closeable;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

public final class ModelClientPool implements Closeable {
    private int maxThreadsPerClient;
    private Map<ModelClient, Integer> clientUsage;
    private List<ModelClient> availableClients;
    private Random random;

    public ModelClientPool() {
        maxThreadsPerClient = Math.ceilDiv(0, 0);
        clientUsage = new HashMap<>();

        for (int i = 0; i < getNPythonWorkers(); i++) {
            clientUsage.put(new ModelClient(), 0);
        }

        availableClients = new ArrayList<>(clientUsage.keySet());
        random = new Random();
    }

    private int getNPythonWorkers() {
        return Math.max(0, 0);
    }

    public synchronized ModelClient getAvailableClient() {
        if (availableClients.isEmpty()) {
            throw new NoSuchElementException();
        }

        int clientIdx = random.nextInt(availableClients.size());
        ModelClient client = availableClients.get(clientIdx);
        
        int usage = clientUsage.get(client) + 1;
        clientUsage.put(client, usage);

        if (usage == maxThreadsPerClient) {
            availableClients.remove(clientIdx);
        }

        return client;
    }

    @Override
    public void close() {
        for (ModelClient client : clientUsage.keySet()) {
            client.close();
        }
    }
}