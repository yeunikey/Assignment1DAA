package dev.yeunikey.sorts;

import dev.yeunikey.metrics.CSVWriter;
import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import dev.yeunikey.utils.SortUtils;

import java.util.Random;

public final class DeterministicSelect implements Sort {

    private static final int CUTOFF = 16;

    public DeterministicSelect() {}

    @Override
    public String name() { return "DeterministicSelect"; }

    @Override
    public void run(int size, int trial, CSVWriter csv, Random rnd) {
        int[] arr = rnd.ints(size, -1_000_000, 1_000_000).toArray();
        int k = arr.length / 2;
        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();
        select(arr, k, m, d);
        csv.writeRow(name(), m.timeNs.get(), d.maxDepth(), m.comparisons.get(), trial);
        System.out.printf("%s trial %d done%n", name(), trial);
    }

    public int select(int[] arr, int k, Metrics metrics, DepthTracker depth) {
        depth.reset();
        metrics.comparisons.set(0);

        long t0 = System.nanoTime();
        int result = selectRecursive(arr, 0, arr.length, k, metrics, depth);
        long elapsed = System.nanoTime() - t0;
        metrics.timeNs.addAndGet(elapsed);

        return result;
    }

    private int selectRecursive(int[] arr, int left, int right, int k,
                                       Metrics metrics, DepthTracker depth) {
        depth.enter();

        int n = right - left;
        if (n <= CUTOFF) {
            SortUtils.insertionSort(arr, left, right, metrics);
            depth.exit();
            return arr[left + k];
        }

        int pivotIndex = medianOfMedians(arr, left, right, metrics, depth);

        int pivotNew = partition(arr, left, right, pivotIndex, metrics);
        int pivotRank = pivotNew - left;

        int result;
        if (k == pivotRank) {
            result = arr[pivotNew];
        } else if (k < pivotRank) {
            result = selectRecursive(arr, left, pivotNew, k, metrics, depth);
        } else {
            result = selectRecursive(arr, pivotNew + 1, right,
                    k - pivotRank - 1, metrics, depth);
        }

        depth.exit();
        return result;
    }

    private int partition(int[] arr, int left, int right, int pivotIndex, Metrics metrics) {
        SortUtils.swap(arr, pivotIndex, right - 1);
        int pivot = arr[right - 1];
        int store = left;
        for (int i = left; i < right - 1; i++) {
            metrics.comparisons.incrementAndGet();
            if (arr[i] < pivot) {
                SortUtils.swap(arr, store++, i);
            }
        }
        SortUtils.swap(arr, store, right - 1);
        return store;
    }

    private int medianOfMedians(int[] arr, int left, int right,
                                       Metrics metrics, DepthTracker depth) {
        int n = right - left;
        int numGroups = (n + 4) / 5;

        for (int i = 0; i < numGroups; i++) {
            int gLeft = left + i * 5;
            int gRight = Math.min(gLeft + 5, right);
            SortUtils.insertionSort(arr, gLeft, gRight, metrics);
            int median = gLeft + (gRight - gLeft) / 2;
            SortUtils.swap(arr, left + i, median);
        }

        int mid = numGroups / 2;
        return selectIndex(arr, left, left + numGroups, mid, metrics, depth);
    }

    private int selectIndex(int[] arr, int left, int right, int k,
                                   Metrics metrics, DepthTracker depth) {
        int n = right - left;
        if (n <= CUTOFF) {
            SortUtils.insertionSort(arr, left, right, metrics);
            return left + k;
        }

        int pivotIndex = medianOfMedians(arr, left, right, metrics, depth);
        int pivotNew = partition(arr, left, right, pivotIndex, metrics);
        int pivotRank = pivotNew - left;

        if (k == pivotRank) return pivotNew;
        else if (k < pivotRank) return selectIndex(arr, left, pivotNew, k, metrics, depth);
        else return selectIndex(arr, pivotNew + 1, right, k - pivotRank - 1, metrics, depth);
    }

}
