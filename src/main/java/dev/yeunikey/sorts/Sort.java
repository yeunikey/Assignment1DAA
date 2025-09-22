package dev.yeunikey.sorts;

import dev.yeunikey.metrics.CSVWriter;

import java.util.Random;

public interface Sort {

    String name();
    void run(int size, int trial, CSVWriter csv, Random rnd);

}
