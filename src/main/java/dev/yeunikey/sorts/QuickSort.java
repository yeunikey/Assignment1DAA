package dev.yeunikey.sorts;

import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import dev.yeunikey.utils.SortUtils;

import java.util.Random;

public final class QuickSort {

    private static final Random rnd = new Random();
    private static final int CUTOFF = 16;

    private QuickSort() {}

    public static void sort(int[] arr, Metrics metrics, DepthTracker depth) {
        depth.reset();
        metrics.comparisons.set(0);
        metrics.allocations.set(0);

        long t0 = System.nanoTime();
        quicksort(arr, 0, arr.length, metrics, depth);
        long elapsed = System.nanoTime() - t0;
        metrics.timeNs.addAndGet(elapsed);
    }

    private static void quicksort(int[] arr, int lo, int hi,
                                  Metrics metrics, DepthTracker depth) {
        while (hi - lo > 1) {
            int n = hi - lo;
            if (n <= CUTOFF) {
                insertionSort(arr, lo, hi, metrics);
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

    private static int randomizedPartition(int[] arr, int lo, int hi, Metrics metrics) {
        int pivotIndex = lo + rnd.nextInt(hi - lo);
        SortUtils.swap(arr, pivotIndex, hi - 1);
        return SortUtils.partition(arr, lo, hi, metrics);
    }

    private static void insertionSort(int[] arr, int lo, int hi, Metrics metrics) {
        for (int i = lo + 1; i < hi; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= lo) {
                metrics.comparisons.incrementAndGet();
                if (arr[j] <= key) break;
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

}
