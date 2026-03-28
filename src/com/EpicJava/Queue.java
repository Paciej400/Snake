package com.EpicJava;

import com.EpicJava.Main;

import java.util.Arrays;

public class Queue {
    private Main.Direction[] queue;
    private int count;
    private int first = -1;
    private int firstEmpty;

    public Queue() {
        queue = new Main.Direction[6];
    }
    public Queue(int capacity) {
        queue = new Main.Direction[Math.max(capacity, 6)];
    }
    public int size() {
        return count;
    }
    public boolean isEmpty() {
        return count == 0;
    }
    public void enQueue (Main.Direction value) {
        if (count == 0) {
            first = 0;
            firstEmpty = 0;
        }
        if (count == queue.length) {
            Main.Direction[] newQueue = new Main.Direction[(int) (queue.length * 1.5)];
            for (int i = 0; i < queue.length; i++) {
                if (first + i < queue.length) {
                    newQueue[i] = queue[first + i];
                } else {
                    newQueue[i] = queue[i - first];
                }
            }
            queue = newQueue;
            first = 0;
            firstEmpty = count;
        }
        queue[firstEmpty] = value;
        count++;
        firstEmpty++;
        if (firstEmpty == queue.length) {
            firstEmpty = 0;
        }
    }
    public Main.Direction deQueue() {
        Main.Direction removed = queue[first];
        queue[first] = null;
        first = (first + 1) % queue.length;
        count--;
        return removed;
    }

    @Override
    public String toString() {
        return "Queue{" +
                "queue=" + Arrays.toString(queue) +
                '}';
    }
}