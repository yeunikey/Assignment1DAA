# Assignment 1 – Divide and Conquer Algorithms

## Overview
This project implements and analyses classic **Divide and Conquer (D&C)** algorithms.  

The focus is on:
- Safe recursion patterns
- Runtime recurrence analysis (Master Theorem & Akra–Bazzi intuition)
- Experimental validation with metrics (time, recursion depth, comparisons/allocations)

All work is tracked in GitHub with proper branching and commit workflow.

---

## Implemented Algorithms
1. **MergeSort** (Case 2 of Master Theorem)
    - Linear merge with reusable buffer
    - Small-n cut-off (Insertion Sort)

2. **QuickSort** (robust version)
    - Randomized pivot
    - Recurse on smaller partition, iterate on larger one

3. **Deterministic Select (Median-of-Medians)**
    - Group by 5, median-of-medians pivot
    - In-place partition
    - Recurse only on needed side (prefer smaller side)

4. **Closest Pair of Points (2D)**
    - Sort by x-coordinate, recursive split
    - “Strip” check by y-order (7–8 neighbour scan)

---

## Metrics
For each algorithm, we collect:
- **Execution time**
- **Recursion depth**
- **Number of comparisons**

Results are exported to CSV for further plotting.

---

## Analysis
Each algorithm will include:
- Recurrence relation and solution (Θ-notation)
- Method of analysis (Master theorem / Akra–Bazzi intuition)
- Comparison between **theory** and **measured results**

---

## Testing
- Sorting correctness on random and adversarial arrays
- Verify recursion depth is bounded
- Selection validated against `Arrays.sort(a)[k]`
- Closest Pair validated against O(n²) brute force (small n ≤ 2000)

---

## Build & Testing
```bash
# Run build
mvn clean package

# Run all tests
mvn test
```

---

## How to run

```bash
# Run main program (after build)
java -jar assignment-1.0-SNAPSHOT.jar \
    --size 10000 \
    --trials 3 \
    --output results.csv \
    --algo quicksort \
    --rnd 42
```

### --size <N>
Number of elements to process (array size for sorting algorithms or number of points for ClosestPair).  
**Example:** `--size 10000` → array of 10,000 elements or 10,000 points.

### --trials <N>
Number of times to run the algorithm for averaging metrics.  
**Example:** `--trials 3` → each algorithm runs 3 times.

### --output <filename>
CSV file where results will be saved.  
**Example:** `--output results.csv` → will create a CSV with columns: algorithm, time (ns), recursion depth, number of comparisons, trial number.

### --algo <name|all>
Algorithm to run. Supported values:
- `quicksort`
- `mergesort`
- `deterministicselect`
- `closestpair`

### --rnd <seed>
Seed for the random number generator.  
**Example:** `--rnd 42` → ensures reproducible arrays/points across runs.

---

# Benchmark

```bash
mvn exec:java -Dexec.mainClass=org.openjdk.jmh.Main "-Dexec.args=dev.yeunikey.bench.SelectVsSortBenchmark -bm avgt -wi 5 -i 3 -rf csv -rff results.csv"
```