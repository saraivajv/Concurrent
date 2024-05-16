package benchmark;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.simplilearn.mavenproject.LevenshteinDistancePlatformSemaphore;
import com.simplilearn.mavenproject.LevenshteinDistancePlatformVolatile;
import com.simplilearn.mavenproject.LevenshteinDistanceAccumulator;
import com.simplilearn.mavenproject.LevenshteinDistanceAdder;
import com.simplilearn.mavenproject.LevenshteinDistanceAtomic;
import com.simplilearn.mavenproject.LevenshteinDistancePlatformMutex;
import com.simplilearn.mavenproject.LevenshteinDistanceSerial;
import com.simplilearn.mavenproject.LevenshteinDistanceVirtualMutex;
import com.simplilearn.mavenproject.LevenshteinDistanceVirtualSemaphore;
import com.simplilearn.mavenproject.LevenshteinDistanceVirtualVolatile;

public class JMHRunner extends AbstractJavaSamplerClient{
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
//        		.include(LevenshteinDistanceSerial.class.getSimpleName())
//        		.include(LevenshteinDistancePlatformMutex.class.getSimpleName())
//        		.include(LevenshteinDistancePlatformSemaphore.class.getSimpleName())
//        		.include(LevenshteinDistanceVirtualMutex.class.getSimpleName())
//        		.include(LevenshteinDistanceVirtualSemaphore.class.getSimpleName())
//        		.include(LevenshteinDistancePlatformVolatile.class.getSimpleName())
        		.include(LevenshteinDistanceVirtualVolatile.class.getSimpleName())
//        		.include(LevenshteinDistanceAccumulator.class.getSimpleName())
//        		.include(LevenshteinDistanceAdder.class.getSimpleName())
//        		.include(LevenshteinDistanceAtomic.class.getSimpleName())
                .warmupIterations(1)
                .shouldDoGC(true)
                .measurementIterations(3).forks(1)
                .addProfiler(GCProfiler.class)
                .addProfiler(StackProfiler.class)
                .jvmArgs("-server", "-Xms2048m", "-Xmx10240m").build();
        new Runner(options).run();
    }
    
    

	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String var1 = context.getParameter("var1");
		String var2 = context.getParameter("var2");
		
		SampleResult result = new SampleResult();
		result.sampleStart();
		result.setSampleLabel("Test Sample");
		LevenshteinDistanceSerial levSerial = new LevenshteinDistanceSerial();
		
		if(levSerial.calculateLevenshteinDistance(var1, var2) >= 3) {
			result.sampleEnd();
			result.setResponseCode("200");
			result.setResponseMessage("OK");
			result.setSuccessful(true);

		} else {
			result.sampleEnd();
			result.setResponseCode("500");
			result.setResponseMessage("NOK");
			result.setSuccessful(false);
		}
		
		return result;
	}
//	
//	@Override public Arguments getDefaultParameters() {
//		Arguments defaultParameters = new Arguments();
//		defaultParameters.addArgument("var1","tour");
//		defaultParameters.addArgument("var2","your");
//		return defaultParameters; 
//	} 
}