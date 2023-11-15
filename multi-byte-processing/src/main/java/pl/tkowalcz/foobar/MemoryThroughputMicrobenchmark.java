package pl.tkowalcz.foobar;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorOperators;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
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
public class MemoryThroughputMicrobenchmark {

    private int[] data;

    @Setup
    public void setup() {
        data = ThreadLocalRandom.current()
                .ints()
                .limit(1024 * 1024 * 256)
                .toArray();

        System.out.println("Species preferred: " + IntVector.SPECIES_PREFERRED);
    }

    //    @Benchmark
    public int simpleLoop() {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            int datum = data[i];
            result += datum;
        }

        return result;
    }

    @Benchmark
    public long vectorisedLoop() {
        int length = IntVector.SPECIES_PREFERRED.loopBound(data.length);

        Vector<Integer> accumulator = IntVector.SPECIES_PREFERRED.zero();
        for (int i = 0; i < length; i += IntVector.SPECIES_PREFERRED.length()) {
            accumulator.add(IntVector.fromArray(IntVector.SPECIES_PREFERRED, data, i));
        }

        return accumulator.reduceLanesToLong(VectorOperators.ADD);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MemoryThroughputMicrobenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
//                .addProfiler(LinuxPerfProfiler.class, "events=branch-misses")
                .addProfiler(LinuxPerfAsmProfiler.class)
                .jvmArgsAppend("--add-modules", "jdk.incubator.vector")
                .addProfiler(GCProfiler.class)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
