package game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {

    private AtomicInteger receivedAtomicCounter = new AtomicInteger();
    private AtomicInteger sentAtomicCounter = new AtomicInteger();

    private Queue<String> incomingQueue;
    private Queue<String> outgoingQueue;

    private String name;

    private List<String> receivedMessages = new ArrayList<>();
    private boolean free = true;

    public Player(Queue<String> incomingQueue, Queue<String> outgoingQueue, String name) {
        this.incomingQueue = incomingQueue;
        this.outgoingQueue = outgoingQueue;
        this.name = name;
    }

    public AtomicInteger getReceivedAtomicCounter() {
        return receivedAtomicCounter;
    }

    public AtomicInteger getSentAtomicCounter() {
        return sentAtomicCounter;
    }

    public Queue<String> getIncomingQueue() {
        return incomingQueue;
    }

    public Queue<String> getOutgoingQueue() {
        return outgoingQueue;
    }

    public String getName() {
        return name;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }
}
