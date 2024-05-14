package pl.tkowalcz.geecon2024.done;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.RunnerException;
import pl.tkowalcz.geecon2024.Conditional;
import pl.tkowalcz.geecon2024.Microbenchmark;

public class ConditionalMicrobenchmark extends Microbenchmark {

    private static final int TEST_ARRAY_SIZE = 1000_0000;

    @State(Scope.Benchmark)
    public static class Data {
        private int[] sales;

        @Setup
        public void setUp() {
            sales = randomArrayOfInt(TEST_ARRAY_SIZE);
        }
    }

    @Benchmark
    public float selectWhere(Data data) {
        return pl.tkowalcz.geecon2024.Conditional.selectWhere(data.sales);
    }

    @Benchmark
    public float cosineSimilarityWithVectors(Data data) {
        return Conditional.selectWhereVectorised(data.sales);
    }

    public static void main(String[] args) throws RunnerException {
        runWith(
                CosineSimilarityMicrobenchmark.class.getSimpleName(),
                GCProfiler.class,
                ""
        );
    }
}
