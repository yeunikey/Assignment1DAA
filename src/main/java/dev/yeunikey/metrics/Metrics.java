package dev.yeunikey.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class Metrics {

    public final AtomicLong comparisons = new AtomicLong();
    public final AtomicLong allocations = new AtomicLong();
    public final AtomicLong timeNs = new AtomicLong();

}