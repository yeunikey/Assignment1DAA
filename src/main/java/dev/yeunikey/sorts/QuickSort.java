package dev.yeunikey.sorts;

import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;

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
        quicksort(arr, 0, arr.length - 1, metrics, depth);
        long elapsed = System.nanoTime() - t0;
        metrics.timeNs.addAndGet(elapsed);
    }

    private static void quicksort(int[] arr, int lo, int hi,
                                  Metrics metrics, DepthTracker depth) {
        while (lo < hi) {
            int n = hi - lo + 1;
            if (n <= CUTOFF) {
                insertionSort(arr, lo, hi, metrics);
                return;
            }

            depth.enter();
            int p = partition(arr, lo, hi, metrics);

            if (p - lo < hi - p) {
                quicksort(arr, lo, p - 1, metrics, depth);
                lo = p + 1;
            } else {
                quicksort(arr, p + 1, hi, metrics, depth);
                hi = p - 1;
            }
            depth.exit();
        }
    }

    private static int partition(int[] arr, int lo, int hi, Metrics metrics) {

        int pivotIndex = lo + rnd.nextInt(hi - lo + 1);
        swap(arr, pivotIndex, hi);

        int pivot = arr[hi];
        int i = lo;

        for (int j = lo; j < hi; j++) {
            metrics.comparisons.incrementAndGet();
            if (arr[j] <= pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, hi);
        return i;
    }

    private static void insertionSort(int[] arr, int lo, int hi, Metrics metrics) {
        for (int i = lo + 1; i <= hi; i++) {
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

    private static void swap(int[] arr, int i, int j) {
        if (i != j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }

}