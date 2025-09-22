package dev.yeunikey.sorts;

import dev.yeunikey.metrics.Metrics;
import dev.yeunikey.metrics.DepthTracker;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class DeterministicSelectTest {

    private final Random rnd = new Random();

    @Test
    public void testRandomTrials() {
        for (int trial = 0; trial < 100; trial++) {
            int n = 200 + rnd.nextInt(300);
            int[] arr = rnd.ints(n, 0, 1000).toArray();
            int[] copy = Arrays.copyOf(arr, n);

            int k = rnd.nextInt(n);

            Metrics m = new Metrics();
            DepthTracker d = new DepthTracker();

            int result = new DeterministicSelect().select(arr, k, m, d);

            Arrays.sort(copy);
            assertEquals(copy[k], result);

            System.out.printf("Deterministic Select, Comparisons=%d, Depth=%d, Time=%d nanosec.%n", m.comparisons.get(), d.maxDepth(), m.timeNs.get());
        }
    }

}
