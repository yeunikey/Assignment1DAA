package dev.yeunikey.sorts;

import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class ClosestPairTest {

    @Test
    public void testSmallFixedPoints() {
        ClosestPair.Point[] pts = {
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(3, 4), // dist = 5
                new ClosestPair.Point(7, 1),
                new ClosestPair.Point(1, 1)  // dist = sqrt(2)
        };

        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();

        double fast = ClosestPair.findClosest(pts, m, d);
        double brute = ClosestPair.bruteForce(pts, 0, pts.length, new Metrics());

        Assert.assertEquals(brute, fast, 1e-9);
        Assert.assertTrue(d.maxDepth() > 0);

        System.out.printf("Closest Pair, Comparisons=%d, Depth=%d, Time=%d ns.%n",
                m.comparisons.get(), d.maxDepth(), m.timeNs.get());
    }

    @Test
    public void testRandomSmallArrays() {
        Random rnd = new Random(42);
        for (int n = 10; n <= 2000; n *= 2) {
            ClosestPair.Point[] pts = new ClosestPair.Point[n];
            for (int i = 0; i < n; i++) {
                pts[i] = new ClosestPair.Point(rnd.nextInt(10_000), rnd.nextInt(10_000));
            }

            Metrics m = new Metrics();
            DepthTracker d = new DepthTracker();

            double fast = ClosestPair.findClosest(pts, m, d);
            double brute = ClosestPair.bruteForce(pts, 0, pts.length, new Metrics());

            Assert.assertEquals("Mismatch at n=" + n, brute, fast, 1e-9);

            System.out.printf("Closest Pair, Comparisons=%d, Depth=%d, Time=%d ns.%n",
                    n, m.comparisons.get(), d.maxDepth(), m.timeNs.get());
        }
    }

    @Test
    public void testRandomLargeArray() {
        Random rnd = new Random(123);
        int n = 100_000;
        ClosestPair.Point[] pts = new ClosestPair.Point[n];
        for (int i = 0; i < n; i++) {
            pts[i] = new ClosestPair.Point(rnd.nextInt(), rnd.nextInt());
        }

        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();

        double fast = ClosestPair.findClosest(pts, m, d);

        Assert.assertTrue(fast >= 0); // sanity check

        System.out.printf("Closest Pair, Comparisons=%d, Depth=%d, Time=%d ns.%n",
                n, m.comparisons.get(), d.maxDepth(), m.timeNs.get());
    }

}
