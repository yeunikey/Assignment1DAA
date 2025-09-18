package dev.yeunikey.sorts;

import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import dev.yeunikey.utils.SortUtils;

public final class DeterministicSelect {

    private static final int CUTOFF = 16;

    private DeterministicSelect() {}

    public static int select(int[] arr, int k, Metrics metrics, DepthTracker depth) {
        depth.reset();
        metrics.comparisons.set(0);

        long t0 = System.nanoTime();
        int result = selectRecursive(arr, 0, arr.length, k, metrics, depth);
        long elapsed = System.nanoTime() - t0;
        metrics.timeNs.addAndGet(elapsed);

        return result;
    }

    private static int selectRecursive(int[] arr, int left, int right, int k,
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

    private static int partition(int[] arr, int left, int right, int pivotIndex, Metrics metrics) {
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

    private static int medianOfMedians(int[] arr, int left, int right,
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

    private static int selectIndex(int[] arr, int left, int right, int k,
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
