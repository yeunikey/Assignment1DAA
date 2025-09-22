package dev.yeunikey;

import dev.yeunikey.metrics.CSVWriter;
import dev.yeunikey.sorts.*;

import java.util.Map;
import java.util.Random;

public class SortCLI {

    public static void main(String[] args) throws Exception {
        int size = 10_000;
        int trials = 3;
        String output = "results.csv";
        String algo = "";
        int rnd = 42;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--size": size = Integer.parseInt(args[++i]); break;
                case "--trials": trials = Integer.parseInt(args[++i]); break;
                case "--output": output = args[++i]; break;
                case "--algo": algo = args[++i].toLowerCase(); break;
                case "--rnd": rnd = Integer.parseInt(args[++i]); break;
            }
        }

        Random random = new Random(rnd);

        Map<String, Sort> algos = Map.of(
                "quicksort", new QuickSort(),
                "mergesort", new MergeSort(),
                "deterministicselect", new DeterministicSelect(),
                "closestpair", new ClosestPair()
        );

        try (CSVWriter csv = new CSVWriter(output)) {

            if (!algos.keySet().contains(algo)) {
                System.out.println("Uknown algo: " + algo);
                return;
            }

            Sort s = algos.get(algo);
            if (s == null) {
                System.err.println("Unknown algorithm: " + algo);
                System.exit(1);
            }
            s.run(size, trials, csv, random);
        }

        System.out.println("Finished. Results written to " + output);
    }

}
