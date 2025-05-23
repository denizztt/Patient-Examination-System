package src;

import java.util.*;

public class MaxHeap {
    private PriorityQueue<Hasta> heap = new PriorityQueue<>();

    public void ekle(Hasta h) { heap.add(h); }
    public Hasta cikar() { return heap.poll(); }
    public Hasta peek() { return heap.peek(); }
    public boolean bosMu() { return heap.isEmpty(); }
    public List<Hasta> getHeapList() { return new ArrayList<>(heap); }
} 