package pl.tkowalcz.geecon2024;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

import java.util.stream.IntStream;

@SuppressWarnings("FieldCanBeLocal")
public class DoubleSumMicrobenchmark extends Microbenchmark {

    public static final int TEST_ARRAY_SIZE = 1024;

    public static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    @State(Scope.Benchmark)
    public static class Data {
        private double[] input1;
        private double[] input2;
        private double[] result;

        @Setup
        public void setUp() {
            input1 = IntStream.range(0, TEST_ARRAY_SIZE)
                    .mapToDouble(value -> (double) value)
                    .toArray();

            input2 = IntStream.range(0, TEST_ARRAY_SIZE)
                    .mapToDouble(value -> (double) value)
                    .toArray();

            result = new double[input1.length];
        }
    }

    @Benchmark
    public void add(Data data) {
        double[] input1 = data.input1;
        double[] input2 = data.input2;
        double[] result = data.result;

        int loopBound = SPECIES.loopBound(input1.length);
        int i = 0;
        for (; i < loopBound; i += SPECIES.length()) {
            DoubleVector vector1 = DoubleVector.fromArray(SPECIES, input1, i);
            DoubleVector vector2 = DoubleVector.fromArray(SPECIES, input2, i);

            vector1.add(vector2).intoArray(result, i);
        }

        for (; i < input1.length; i++) {
            data.result[i] = input1[i] + input2[i];
        }
    }

    public static void main(String[] args) throws RunnerException {
        run(DoubleSumMicrobenchmark.class.getSimpleName());
    }
}
