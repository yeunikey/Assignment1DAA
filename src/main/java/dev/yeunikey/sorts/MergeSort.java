package dev.yeunikey.sorts;

import dev.yeunikey.metrics.CSVWriter;
import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import dev.yeunikey.utils.SortUtils;

import java.util.Random;

public final class MergeSort implements Sort {

    private static final int CUTOFF = 16;

    public MergeSort() {}

    @Override
    public String name() { return "MergeSort"; }

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

        int[] buffer = new int[arr.length];

        long t0 = System.nanoTime();
        sortRecursive(arr, buffer, 0, arr.length, metrics, depth);
        long elapsed = System.nanoTime() - t0;
        metrics.timeNs.addAndGet(elapsed);
    }

    private void sortRecursive(int[] arr, int[] buffer,
                                      int left, int right,
                                      Metrics metrics, DepthTracker depth) {
        depth.enter();

        int n = right - left;
        if (n <= CUTOFF) {
            SortUtils.insertionSort(arr, left, right, metrics);
            depth.exit();
            return;
        }

        int mid = (left + right) >>> 1;
        sortRecursive(arr, buffer, left, mid, metrics, depth);
        sortRecursive(arr, buffer, mid, right, metrics, depth);
        merge(arr, buffer, left, mid, right, metrics);

        depth.exit();
    }

    private void merge(int[] arr, int[] buffer,
                              int left, int mid, int right,
                              Metrics metrics) {
        int i = left, j = mid, k = left;

        while (i < mid && j < right) {
            metrics.comparisons.incrementAndGet();
            if (arr[i] <= arr[j]) buffer[k++] = arr[i++];
            else buffer[k++] = arr[j++];
        }
        while (i < mid) buffer[k++] = arr[i++];
        while (j < right) buffer[k++] = arr[j++];

        System.arraycopy(buffer, left, arr, left, right - left);
    }

}