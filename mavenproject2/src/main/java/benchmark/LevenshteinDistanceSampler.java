package benchmark;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.simplilearn.mavenproject.LevenshteinDistanceVirtualMutex;

public class LevenshteinDistanceSampler extends AbstractJavaSamplerClient {

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String var1 = context.getParameter("palavra1");
        String var2 = context.getParameter("palavra2");

        SampleResult result = new SampleResult();
        result.sampleStart();
        result.setSampleLabel("Levenshtein Distance Test");

        LevenshteinDistanceVirtualMutex levenshteinDistance = new LevenshteinDistanceVirtualMutex();
        int distance = levenshteinDistance.calculateLevenshteinDistance(var1, var2);

        result.sampleEnd();
        if (distance <= 3) {
            result.setResponseCode("200");
            result.setResponseMessage("OK");
            result.setSuccessful(true);
        } else {
            result.setResponseCode("500");
            result.setResponseMessage("NOK");
            result.setSuccessful(false);
        }

        return result;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("palavra1", "tour");
        defaultParameters.addArgument("palavra2", "your");
        return defaultParameters;
    }
}
