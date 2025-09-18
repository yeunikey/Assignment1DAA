package dev.yeunikey.sorts;

import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;

import java.util.Arrays;
import java.util.Comparator;

public final class ClosestPair {

    private ClosestPair() {}

    public static double findClosest(Point[] points, Metrics metrics, DepthTracker depth) {
        depth.reset();
        metrics.comparisons.set(0);
        metrics.timeNs.set(0);

        Point[] pts = points.clone();
        Arrays.sort(pts, Comparator.comparingInt(p -> p.x)); // O(n log n)

        long t0 = System.nanoTime();
        double result = closestRecursive(pts, 0, pts.length, metrics, depth);
        long elapsed = System.nanoTime() - t0;
        metrics.timeNs.addAndGet(elapsed);

        return result;
    }

    private static double closestRecursive(Point[] pts, int left, int right,
                                           Metrics metrics, DepthTracker depth) {
        depth.enter();
        try {
            int n = right - left;
            if (n <= 3) {
                return bruteForce(pts, left, right, metrics);
            }

            int mid = (left + right) >>> 1;
            int midX = pts[mid].x;

            double dLeft = closestRecursive(pts, left, mid, metrics, depth);
            double dRight = closestRecursive(pts, mid, right, metrics, depth);
            double d = Math.min(dLeft, dRight);

            Point[] strip = new Point[n];
            int m = 0;
            for (int i = left; i < right; i++) {
                metrics.comparisons.incrementAndGet();
                if (Math.abs(pts[i].x - midX) < d) {
                    strip[m++] = pts[i];
                }
            }

            Arrays.sort(strip, 0, m, Comparator.comparingInt(p -> p.y));

            for (int i = 0; i < m; i++) {
                for (int j = i + 1; j < m && (strip[j].y - strip[i].y) < d; j++) {
                    metrics.comparisons.incrementAndGet();
                    d = Math.min(d, dist(strip[i], strip[j]));
                }
            }

            return d;
        } finally {
            depth.exit();
        }
    }

    public static double bruteForce(Point[] pts, int left, int right, Metrics metrics) {
        double d = Double.POSITIVE_INFINITY;
        for (int i = left; i < right; i++) {
            for (int j = i + 1; j < right; j++) {
                metrics.comparisons.incrementAndGet();
                d = Math.min(d, dist(pts[i], pts[j]));
            }
        }
        return d;
    }

    private static double dist(Point a, Point b) {
        double dx = (double) a.x - b.x;
        double dy = (double) a.y - b.y;
        return Math.hypot(dx, dy);
    }

    public static class Point {
        public final int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }

}
