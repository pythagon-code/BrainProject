package edu.illinois.abhayp4;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;

final class ModelClientService {
    static ModelClientService service;

    private int maxThreadsPerClient;
    private Map<ModelClient, Integer> clientUsage;
    private List<ModelClient> availableClients;
    private Random random;

    public static ModelClientService getService() {
        if (service == null) {
            synchronized (ModelClientService.class) {
                if (service == null) {
                    service = new ModelClientService();
                }
            }
        }
        return service;
    }

    public synchronized ModelClient getAvailableClient() {
        if (availableClients.isEmpty()) {
            throw new IllegalStateException("No available clients remaining.");
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

    private ModelClientService() {
        maxThreadsPerClient = Math.ceilDiv(GlobalState.getNThreads(), getNPythonWorkers());
        clientUsage = new HashMap<>();

        for (int i = 0; i < getNPythonWorkers(); i++) {
            clientUsage.put(new ModelClient(), 0);
        }
        
        availableClients = new ArrayList<>(clientUsage.keySet());
        random = new Random();
    }


    private int getNPythonWorkers() {
        return Math.max(GlobalState.getNPythonWorkers(), GlobalState.getNThreads());
    }
}