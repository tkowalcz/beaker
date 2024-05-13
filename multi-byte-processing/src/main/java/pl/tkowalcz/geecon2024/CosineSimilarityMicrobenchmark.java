package pl.tkowalcz.geecon2024;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.RunnerException;

public class CosineSimilarityMicrobenchmark extends Microbenchmark {

    private static final int TEST_ARRAY_SIZE = 3072;

    @State(Scope.Benchmark)
    public static class Data {
        private float[] input1;
        private float[] input2;

        @Setup
        public void setUp() {
            input1 = floatArray(TEST_ARRAY_SIZE);
            input2 = floatArray(TEST_ARRAY_SIZE);
        }
    }

    @Benchmark
    public float cosineSimilarity(Data data) {
        return CosineSimilarity.cosineSimilarity(data.input1, data.input2);
    }

    @Benchmark
    public float cosineSimilarityWithVectors(Data data) {
        return CosineSimilarity.cosineSimilarityWithVectors(data.input1, data.input2);
    }

    public static void main(String[] args) throws RunnerException {
        runWith(
                CosineSimilarityMicrobenchmark.class.getSimpleName(),
                GCProfiler.class,
                ""
        );
    }
}
