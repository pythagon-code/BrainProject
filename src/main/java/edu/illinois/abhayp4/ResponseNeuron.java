package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class ResponseNeuron extends RootObject {
        @JsonCreator
        public ResponseNeuron(@JsonProperty("Name") String name) {
            super(name);
        }
}
