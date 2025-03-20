package edu.illinois.abhayp4;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class Channel {
    public final String name;
    private final Queue<String> messages = new ConcurrentLinkedQueue<>();
    public final SourceEndpoint source;
    public final TargetEndpoint target;

    public Channel(String name) {
        this.name = name;
        this.source = new SourceEndpoint(this);
        this.target = new TargetEndpoint(this);
    }

    private class Endpoint {
        public final Channel channel;

        public Endpoint(Channel channel) {
            this.channel = channel;
        }
    }

    public class SourceEndpoint extends Endpoint {
        public SourceEndpoint(Channel channel) {
            super(channel);
        }

        public void send(String msg) {
            System.out.println(channel.name + " sending message: " + msg);
            channel.messages.add(msg);
        }
    }

    public class TargetEndpoint extends Endpoint {
        public TargetEndpoint(Channel channel) {
            super(channel);
        }

        public boolean hasMessage() {
            return !channel.messages.isEmpty();
        }

        public String receive() {
            System.out.println(channel.name + " receiving message: " + channel.messages.peek());
            
            if (!hasMessage()) {
                throw new IllegalStateException("No message to receive.");
            }   

            return channel.messages.poll();
        }
    }
}