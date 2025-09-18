package dev.yeunikey.sorts;

import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class MergeSortTest {

    @Test
    public void testSmallArray() {
        int[] arr = {5, 2, 4, 1, 3};
        int[] expected = {1, 2, 3, 4, 5};

        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();

        MergeSort.sort(arr, m, d);

        Assert.assertArrayEquals(expected, arr);
        Assert.assertTrue(d.maxDepth() > 0);

        System.out.printf("Merge Sort, Comparisons=%d, Depth=%d, Time=%d nanosec.%n", m.comparisons.get(), d.maxDepth(), m.timeNs.get());
    }

    @Test
    public void testRandomLargeArray() {
        Random rnd = new Random(42);
        int[] arr = rnd.ints(10_000, -1000, 1000).toArray();
        int[] expected = arr.clone();
        Arrays.sort(expected);

        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();

        MergeSort.sort(arr, m, d);

        Assert.assertArrayEquals(expected, arr);
        System.out.printf("Merge Sort, Comparisons=%d, Depth=%d, Time=%d nanosec.%n", m.comparisons.get(), d.maxDepth(), m.timeNs.get());
    }

    @Test
    public void testAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        int[] expected = arr.clone();

        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();

        MergeSort.sort(arr, m, d);

        Assert.assertArrayEquals(expected, arr);
        System.out.printf("Merge Sort, Comparisons=%d, Depth=%d, Time=%d nanosec.%n", m.comparisons.get(), d.maxDepth(), m.timeNs.get());
    }

}