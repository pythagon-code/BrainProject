/**
 * NamedObject.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

abstract class NamedObject {
    @JsonProperty("Name") public final String name;

    @JsonCreator
    public NamedObject(
        @JsonProperty("Name") String name
    ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + name;
    }
}