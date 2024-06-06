package benchmark;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.simplilearn.mavenproject.LevenshteinDistancePlatformSemaphore;
import com.simplilearn.mavenproject.LevenshteinDistancePlatformVolatile;
import com.simplilearn.mavenproject.LevenshteinDistanceScopedValue;
import com.simplilearn.mavenproject.LevenshteinDistanceAccumulator;
import com.simplilearn.mavenproject.LevenshteinDistanceAdder;
import com.simplilearn.mavenproject.LevenshteinDistanceAtomic;
import com.simplilearn.mavenproject.LevenshteinDistanceCompletableFuture;
import com.simplilearn.mavenproject.LevenshteinDistanceConcurrentCollections;
import com.simplilearn.mavenproject.LevenshteinDistanceForkjoin;
import com.simplilearn.mavenproject.LevenshteinDistanceParallelStreams;
import com.simplilearn.mavenproject.LevenshteinDistancePlatformMutex;
import com.simplilearn.mavenproject.LevenshteinDistanceSerial;
import com.simplilearn.mavenproject.LevenshteinDistanceVirtualMutex;
import com.simplilearn.mavenproject.LevenshteinDistanceVirtualSemaphore;
import com.simplilearn.mavenproject.LevenshteinDistanceVirtualVolatile;

public class JMHRunner{
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
//        		.include(LevenshteinDistanceSerial.class.getSimpleName())
//        		.include(LevenshteinDistancePlatformMutex.class.getSimpleName())
//        		.include(LevenshteinDistancePlatformSemaphore.class.getSimpleName())
//        		.include(LevenshteinDistanceVirtualMutex.class.getSimpleName())
//        		.include(LevenshteinDistanceVirtualSemaphore.class.getSimpleName())
//        		.include(LevenshteinDistancePlatformVolatile.class.getSimpleName())
//        		.include(LevenshteinDistanceVirtualVolatile.class.getSimpleName())
//        		.include(LevenshteinDistanceAccumulator.class.getSimpleName())
//        		.include(LevenshteinDistanceAdder.class.getSimpleName())
//        		.include(LevenshteinDistanceAtomic.class.getSimpleName())
//        		.include(LevenshteinDistanceForkjoin.class.getSimpleName())
//        		.include(LevenshteinDistanceParallelStreams.class.getSimpleName())
//        		.include(LevenshteinDistanceCompletableFuture.class.getSimpleName())
//        		.include(LevenshteinDistanceConcurrentCollections.class.getSimpleName())
        		.include(LevenshteinDistanceScopedValue.class.getSimpleName())
                .warmupIterations(1)
                .shouldDoGC(true)
                .measurementIterations(3).forks(1)
                .addProfiler(GCProfiler.class)
                .addProfiler(StackProfiler.class)
                .jvmArgs("-server", "-Xms2048m", "-Xmx10240m").build();
        new Runner(options).run();
    }
    
}