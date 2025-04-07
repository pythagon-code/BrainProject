/**
 * MyTest.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

import org.junit.Test;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

public class MyTest {
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class MyClass {
        private final int i, j, k;
        public final int l;
        public TextChannel channel;

        @JsonCreator
        public MyClass(
            @JsonProperty("i") int i,
            @JsonProperty("j") int j,
            @JsonProperty("k") int k
        ) {
            this.i = i;
            this.j = j;
            this.k = j;
            this.l = 4;
            channel = null;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof MyClass)) {
                return false;
            }

            MyClass obj = (MyClass) other;
            return (i == obj.i) && (j == obj.j) && (k == obj.k) && (channel == obj.channel);
        }
    }
    
    @Test
    public void testJackson1() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MyClass obj = new MyClass(1, 2, 3);
        obj.channel = new TextChannel("hello");
        String json = objectMapper.writeValueAsString(obj);
        MyClass obj2 = objectMapper.readValue(json, MyClass.class);
        assertNotEquals(obj, obj2);
        obj.channel = null;
        assertEquals(obj, obj2);
        assertEquals(obj2.l, 4);

        System.out.println("testJackson1(): ");
        System.out.println(json);
        System.out.println();
    }
}