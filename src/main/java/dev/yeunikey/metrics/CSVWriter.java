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
        pw.println("Algorithm,TimeNs,Depth,Comparisons,Trial");
    }

    public void writeRow(String algo, long timeNs, int depth, long comps, int trial) {
        pw.printf("%s,%d,%d,%d,%d\n", algo, timeNs, depth, comps, trial);
    }

    public void close(){ pw.close(); }

}
