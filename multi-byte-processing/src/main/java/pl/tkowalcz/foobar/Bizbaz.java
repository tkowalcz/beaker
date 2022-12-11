package pl.tkowalcz.foobar;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @noinspection ALL
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(
        value = 1,
        jvmArgsPrepend = {
                "-XX:+UnlockExperimentalVMOptions",
                "-XX:+EnableVectorSupport",
                "-XX:+EnableVectorReboxing",
                "-XX:+EnableVectorAggressiveReboxing",
                "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
        }
)
public class Bizbaz {

    private int[] data;

    @Setup
    public void setup() {
        data = ThreadLocalRandom.current()
                .ints()
                .limit(1024 * 1024 * 256)
                .toArray();
    }

    //    @Benchmark
    public int simpleLoop() {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 100) {
                result++;
            }
        }

        return result;
    }

    //    @Benchmark
    public int branchlessLoop() {
        int mask = 0b0111_1111_1111_1111;
        int predicate = 100;

        int result = 0;
        for (int i = 0; i < data.length; i++) {
            int datum = data[i] ^ mask;
            int value = (predicate + datum) & ~mask;

            result += value;
        }

        return result;
    }

    //    @Benchmark
    public int vectorizedLoop() {
        int mask = 0b0111_1111_1111_1111;
        VectorSpecies<Integer> species = IntVector.SPECIES_PREFERRED;
        Vector<Integer> maskVector = species.broadcast(mask);

        int predicate = 100;
        Vector<Integer> predicateVector = species.broadcast(predicate);

        int result = 0;
        int loopBound = species.loopBound(data.length);
        for (int i = 0; i < loopBound; i += species.length()) {
            IntVector intVector = IntVector.fromArray(species, data, i);

            intVector = intVector
                    .lanewise(VectorOperators.XOR, maskVector)
                    .lanewise(VectorOperators.ADD, predicateVector)
                    .lanewise(VectorOperators.AND, maskVector.neg());

            result += intVector.reduceLanes(VectorOperators.ADD);
        }

        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Bizbaz.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(GCProfiler.class)
                .addProfiler(LinuxPerfProfiler.class/*, "events=branch-misses"*/)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
