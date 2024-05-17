package com.simplilearn.mavenproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import model.DataSet;

@State(Scope.Benchmark)
public class LevenshteinDistancePlatformMutex {
    private static ThreadLocal<Integer> threadLocalCounter = ThreadLocal.withInitial(() -> 0);
    private static int totalSimWords = 0;
    private static final Lock lock = new ReentrantLock(); // Mutex
    private static final String DATASET_PATH = "C:\\Users\\joaov\\git\\bestmatching\\mavenproject\\src\\main\\java\\com\\simplilearn\\mavenproject\\textao.txt";
    private static final String REFERENCE_WORD = "tour";
    private static final int MAX_DISTANCE = 3;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors(); // Number of available processors

    private static final DataSet DATASET = new DataSet();

    @Setup
    public static final void setup() throws IOException {
        System.out.println(" entrei no setup");
        DATASET.read(DATASET_PATH);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void calculateLevenshteinDistanceBenchmark(Blackhole blackhole) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<List<String>> chunks = chunkList(DATASET.getTextWords(), THREAD_POOL_SIZE);
        List<Future<Integer>> futures = new ArrayList<>();

        for (List<String> chunk : chunks) {
            Callable<Integer> task = () -> processChunk(chunk, blackhole);
            futures.add(executor.submit(task));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Collect the results
        for (Future<Integer> future : futures) {
            try {
                int localCount = future.get();
                lock.lock();
                try {
                    totalSimWords += localCount;
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Quantidade de palavras parecidas encontradas: " + totalSimWords);
    }

    private Integer processChunk(List<String> chunk, Blackhole blackhole) {
        threadLocalCounter.set(0); // Reset thread-local counter for this task
        for (String word : chunk) {
            int distance = calculateLevenshteinDistance(REFERENCE_WORD, word.toLowerCase());
            blackhole.consume(distance);
            if (distance <= MAX_DISTANCE) {
                incrementSimWords();
            }
        }
        return threadLocalCounter.get();
    }

    private void incrementSimWords() {
        threadLocalCounter.set(threadLocalCounter.get() + 1);
    }

    private List<List<String>> chunkList(List<String> list, int numChunks) {
        List<List<String>> chunks = new ArrayList<>();
        int chunkSize = list.size() / numChunks;
        int remainder = list.size() % numChunks;
        int index = 0;
        for (int i = 0; i < numChunks; i++) {
            int size = chunkSize + (i < remainder ? 1 : 0);
            chunks.add(new ArrayList<>(list.subList(index, index + size)));
            index += size;
        }
        return chunks;
    }

    public int calculateLevenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(word1.charAt(i - 1), word2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Math.min(numbers[0], Math.min(numbers[1], numbers[2]));
    }
}
