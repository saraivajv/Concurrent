package com.simplilearn.mavenproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
public class LevenshteinDistanceCompletableFuture {
    private static final String DATASET_PATH = "C:\\Users\\joaov\\git\\textao.txt";
    private static final String REFERENCE_WORD = "tour";
    private static final int MAX_DISTANCE = 3;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static int total = 0;

    private static final DataSet DATASET = new DataSet();

    @Setup
    public static final void setup() throws IOException {
        System.out.println("entrei no setup");
        DATASET.read(DATASET_PATH);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void calculateLevenshteinDistanceBenchmark(Blackhole blackhole) throws ExecutionException, InterruptedException {
        List<String> words = DATASET.getTextWords();
        List<List<String>> chunks = chunkList(words, THREAD_POOL_SIZE);

        // Process chunks in parallel using CompletableFuture and sum the results
        List<CompletableFuture<Integer>> futures = chunks.stream()
            .map(chunk -> CompletableFuture.supplyAsync(() -> processChunk(chunk, blackhole)))
            .collect(Collectors.toList());

        for (CompletableFuture<Integer> future : futures) {
            total += future.get();
        }

        System.out.println("Quantidade de palavras parecidas encontradas: " + total);
    }

    private int processChunk(List<String> chunk, Blackhole blackhole) {
        AtomicInteger localCounter = new AtomicInteger(0);
        chunk.forEach(word -> {
            int distance = calculateLevenshteinDistance(REFERENCE_WORD, word.toLowerCase());
            blackhole.consume(distance);
            if (distance <= MAX_DISTANCE) {
                localCounter.incrementAndGet();
            }
        });
        return localCounter.get();
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
