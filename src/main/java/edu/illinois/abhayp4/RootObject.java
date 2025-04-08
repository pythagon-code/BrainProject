/**
 * RootObject.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

abstract class RootObject {
    @JsonProperty("Name") public final String name;

    @JsonCreator
    public RootObject(
        @JsonProperty("Name") String name
    ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + name;
    }

    @Override
    public boolean equals(Object other) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String thisJson = objectMapper.writeValueAsString(this);
            String otherJson = objectMapper.writeValueAsString(other);
            
            return thisJson.equals(otherJson);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}