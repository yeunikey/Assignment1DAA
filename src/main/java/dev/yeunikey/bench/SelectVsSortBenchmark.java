package dev.yeunikey.bench;

import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import dev.yeunikey.sorts.DeterministicSelect;
import dev.yeunikey.sorts.MergeSort;
import dev.yeunikey.sorts.QuickSort;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class SelectVsSortBenchmark {

    @Param({"1000", "10000", "100000"})
    public int size;

    private int[] array;
    private Random rnd = new Random(42);

    @Setup(Level.Invocation)
    public void setup() {
        array = rnd.ints(size, -1_000_000, 1_000_000).toArray();
    }

    @Benchmark
    public int quickSortBenchmark() {
        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();
        new QuickSort().sort(array.clone(), m, d);
        return array[0];
    }

    @Benchmark
    public int mergeSortBenchmark() {
        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();
        new MergeSort().sort(array.clone(), m, d);
        return array[0];
    }

    @Benchmark
    public int deterministicSelectBenchmark() {
        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();
        return new DeterministicSelect().select(array.clone(), array.length / 2, m, d);
    }

}