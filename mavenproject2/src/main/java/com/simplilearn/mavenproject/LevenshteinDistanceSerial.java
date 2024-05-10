package com.simplilearn.mavenproject;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;
import com.simplilearn.mavenproject.WordReader;
//import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;

public class LevenshteinDistanceSerial{
	private static int simWords = 0;
	
	@Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void calculateLevenshteinDistanceBenchmark(Blackhole blackhole) {
		Scanner scanner = new Scanner(System.in);
        String referenceWord = "tour"; // Palavra de referência
        int maxDistance = 3; // Distância máxima permitida
        
        try {
        	WordReader wordReader = new WordReader("C:\\Users\\joaov\\git\\bestmatching\\mavenproject\\src\\main\\java\\com\\simplilearn\\mavenproject\\textao.txt");
            while (wordReader.hasNextLine()) {
                String line = wordReader.getNextLine();
                String words[] = line.split("\\s+");
                for (String word : words) {
                    int distance = calculateLevenshteinDistance(referenceWord, word.toLowerCase());
                    blackhole.consume(distance);
                    if (distance <= maxDistance) {
                    	simWords++;
                    }
                }
            }
            wordReader.close();
            System.out.println("Quantidade de palavras parecidas encontradas: " +simWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
