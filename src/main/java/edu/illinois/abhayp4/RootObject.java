/**
 * RootObject.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}