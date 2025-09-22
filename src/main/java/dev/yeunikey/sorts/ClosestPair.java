package dev.yeunikey.sorts;

import dev.yeunikey.metrics.CSVWriter;
import dev.yeunikey.metrics.DepthTracker;
import dev.yeunikey.metrics.Metrics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public final class ClosestPair implements Sort {

    public ClosestPair() {}

    @Override
    public String name() { return "ClosestPair"; }

    @Override
    public void run(int size, int trial, CSVWriter csv, Random rnd) {
        Point[] pts = new Point[size];
        for (int i = 0; i < size; i++) {
            pts[i] = new Point(rnd.nextInt(1_000_000), rnd.nextInt(1_000_000));
        }
        Metrics m = new Metrics();
        DepthTracker d = new DepthTracker();
        findClosest(pts, m, d);
        csv.writeRow(name(), m.timeNs.get(), d.maxDepth(), m.comparisons.get(), trial);
        System.out.printf("%s trial %d done%n", name(), trial);
    }

    public double findClosest(Point[] points, Metrics metrics, DepthTracker depth) {
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

    private double closestRecursive(Point[] pts, int left, int right,
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

    public double bruteForce(Point[] pts, int left, int right, Metrics metrics) {
        double d = Double.POSITIVE_INFINITY;
        for (int i = left; i < right; i++) {
            for (int j = i + 1; j < right; j++) {
                metrics.comparisons.incrementAndGet();
                d = Math.min(d, dist(pts[i], pts[j]));
            }
        }
        return d;
    }

    private double dist(Point a, Point b) {
        double dx = (double) a.x - b.x;
        double dy = (double) a.y - b.y;
        return Math.hypot(dx, dy);
    }

    public static class Point {
        public final int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }

}
