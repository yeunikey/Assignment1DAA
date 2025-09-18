package dev.yeunikey.metrics;

public class DepthTracker {

    private final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    private int maxDepth = 0;

    public void enter() {
        int d = depth.get() + 1;
        depth.set(d);
        synchronized (this) {
            if (d > maxDepth) maxDepth = d;
        }
    }

    public void exit() {
        depth.set(depth.get() - 1);
    }

    public int current() { return depth.get(); }
    public synchronized int maxDepth() { return maxDepth; }
    public synchronized void reset() { maxDepth = 0; depth.set(0); }

}