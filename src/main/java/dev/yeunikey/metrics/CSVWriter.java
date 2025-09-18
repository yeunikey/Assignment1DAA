package dev.yeunikey.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public final class CSVWriter implements AutoCloseable {

    private final PrintWriter pw;

    public CSVWriter(String path) throws IOException {
        File file = new File(path);
        System.out.println(file.getAbsolutePath());

        this.pw = new PrintWriter(new FileWriter(file, true));

        if (file.exists() && file.length() == 0) {
            setupHeaders();
        }
    }

    private void setupHeaders() {
        pw.println("Algorithm,n,Trial,TimeNs,Depth,Comparisons,Allocations");
    }

    public void writeRow(String algo, int n, int trial, long timeNs, int depth, long comps, long allocs) {
        pw.printf("%s,%d,%d,%d,%d,%d,%d\n", algo, n, trial, timeNs, depth, comps, allocs);
    }

    public void close(){ pw.close(); }

}
