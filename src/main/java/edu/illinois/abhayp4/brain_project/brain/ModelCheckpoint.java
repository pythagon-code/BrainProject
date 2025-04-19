package edu.illinois.abhayp4.brain_project.brain;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.illinois.abhayp4.brain_project.neurons.RelayNeuronFactory;

record ModelCheckpoint(
    @JsonProperty("SimulatorConfig") SimulatorConfig simulatorConfig,
    @JsonProperty("NeuronFactory") RelayNeuronFactory neuronFactory
) { }