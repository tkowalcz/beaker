package pl.tkowalcz.geecon2024.done;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.RunnerException;
import pl.tkowalcz.geecon2024.CosineSimilarity;
import pl.tkowalcz.geecon2024.Microbenchmark;

public class CosineSimilarityMicrobenchmark extends Microbenchmark {

    private static final int TEST_ARRAY_SIZE = 3072;

    @State(Scope.Benchmark)
    public static class Data {
        private float[] input1;
        private float[] input2;

        @Setup
        public void setUp() {
            input1 = randomArrayOfFloat(TEST_ARRAY_SIZE);
            input2 = randomArrayOfFloat(TEST_ARRAY_SIZE);
        }
    }

    @Benchmark
    public float cosineSimilarity(Data data) {
        return pl.tkowalcz.geecon2024.CosineSimilarity.cosineSimilarity(data.input1, data.input2);
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
