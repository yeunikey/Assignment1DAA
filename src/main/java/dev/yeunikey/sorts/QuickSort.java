package dev.yeunikey.sorts;

import dev.yeunikey.metrics.CSVWriter;
import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import dev.yeunikey.utils.SortUtils;

import java.util.Random;

public final class QuickSort implements Sort {

    private static final Random rnd = new Random();
    private static final int CUTOFF = 16;

    public QuickSort() {}

    @Override
    public String name() {
        return "QuickSort";
    }

    @Override
    public void run(int size, int trial, CSVWriter csv, Random rnd) {
        int[] arr = rnd.ints(size, -1_000_000, 1_000_000).toArray();
        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();
        sort(arr, m, d);
        csv.writeRow(name(), m.timeNs.get(), d.maxDepth(), m.comparisons.get(), trial);
        System.out.printf("%s trial %d done%n", name(), trial);
    }

    public void sort(int[] arr, Metrics metrics, DepthTracker depth) {
        depth.reset();
        metrics.comparisons.set(0);

        long t0 = System.nanoTime();
        quicksort(arr, 0, arr.length, metrics, depth);
        long elapsed = System.nanoTime() - t0;
        metrics.timeNs.addAndGet(elapsed);
    }

    private void quicksort(int[] arr, int lo, int hi,
                                  Metrics metrics, DepthTracker depth) {
        while (hi - lo > 1) {
            int n = hi - lo;
            if (n <= CUTOFF) {
                SortUtils.insertionSort(arr, lo, hi, metrics);
                return;
            }

            depth.enter();
            int p = randomizedPartition(arr, lo, hi, metrics);

            if (p - lo < hi - (p + 1)) {
                quicksort(arr, lo, p, metrics, depth);
                lo = p + 1;
            } else {
                quicksort(arr, p + 1, hi, metrics, depth);
                hi = p;
            }
            depth.exit();
        }
    }

    private int randomizedPartition(int[] arr, int lo, int hi, Metrics metrics) {
        int pivotIndex = lo + rnd.nextInt(hi - lo);
        SortUtils.swap(arr, pivotIndex, hi - 1);
        return SortUtils.partition(arr, lo, hi, metrics);
    }

}
