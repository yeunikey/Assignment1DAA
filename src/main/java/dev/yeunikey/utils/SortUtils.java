package dev.yeunikey.utils;

import dev.yeunikey.metrics.Metrics;

import java.util.Random;

public class SortUtils {

    private static final Random rnd = new Random();

    private SortUtils() {}

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static int partition(int[] arr, int left, int right, Metrics metrics) {
        int pivot = arr[right - 1];
        int i = left;
        for (int j = left; j < right - 1; j++) {
            metrics.comparisons.incrementAndGet();
            if (arr[j] <= pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, right - 1);
        return i;
    }

    public static void shuffle(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            swap(arr, i, j);
        }
    }

    public static void checkBounds(int[] arr, int left, int right) {
        if (left < 0 || right > arr.length || left > right) {
            throw new IllegalArgumentException(
                    "Invalid bounds: left=" + left + ", right=" + right + ", length=" + arr.length
            );
        }
    }

}
