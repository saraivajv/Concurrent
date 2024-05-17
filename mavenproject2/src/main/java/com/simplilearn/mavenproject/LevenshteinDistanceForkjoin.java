package com.simplilearn.mavenproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

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
public class LevenshteinDistanceForkjoin{
    private static volatile int simWords = 0;

    private static final String DATASET_PATH = "C:\\Users\\joaov\\git\\bestmatching\\mavenproject\\src\\main\\java\\com\\simplilearn\\mavenproject\\textao.txt";
    private static final String REFERENCE_WORD = "tour";
    private static final int MAX_DISTANCE = 3;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private static final DataSet DATASET = new DataSet();

    @Setup
    public static final void setup() throws IOException {
        System.out.println("entrei no setup");
        DATASET.read(DATASET_PATH);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void calculateLevenshteinDistanceBenchmark(Blackhole blackhole) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_POOL_SIZE)) {
			List<String> words = DATASET.getTextWords();
			int chunkSize = words.size() / THREAD_POOL_SIZE;
			LevenshteinDistanceTask task = new LevenshteinDistanceTask(words, 0, words.size(), chunkSize, blackhole);
			forkJoinPool.invoke(task);
		}
        System.out.println("Quantidade de palavras parecidas encontradas: " + simWords);
    }

    private static class LevenshteinDistanceTask extends RecursiveTask<Integer> {
        private final List<String> words;
        private final int start;
        private final int end;
        private final int chunkSize;
        private final Blackhole blackhole;

        LevenshteinDistanceTask(List<String> words, int start, int end, int chunkSize, Blackhole blackhole) {
            this.words = words;
            this.start = start;
            this.end = end;
            this.chunkSize = chunkSize;
            this.blackhole = blackhole;
        }

        @Override
        protected Integer compute() {
            if (end - start <= chunkSize) {
                return processChunk();
            } else {
                int mid = (start + end) / 2;
                LevenshteinDistanceTask leftTask = new LevenshteinDistanceTask(words, start, mid, chunkSize, blackhole);
                LevenshteinDistanceTask rightTask = new LevenshteinDistanceTask(words, mid, end, chunkSize, blackhole);
                leftTask.fork();
                int rightResult = rightTask.compute();
                int leftResult = leftTask.join();
                return leftResult + rightResult;
            }
        }

        private Integer processChunk() {
            int localCount = 0;
            for (int i = start; i < end; i++) {
                String word = words.get(i);
                int distance = calculateLevenshteinDistance(REFERENCE_WORD, word.toLowerCase());
                blackhole.consume(distance);
                if (distance <= MAX_DISTANCE) {
                    localCount++;
                }
            }
            synchronized (LevenshteinDistanceVirtualMutex.class) {
                simWords += localCount;
            }
            return localCount;
        }
    }

    public static int calculateLevenshteinDistance(String word1, String word2) {
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
