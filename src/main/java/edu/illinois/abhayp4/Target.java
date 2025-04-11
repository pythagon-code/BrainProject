/**
 * Target.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
sealed interface Target permits TextChannel {
    void enqueue(String message);
}