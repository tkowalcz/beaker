package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.Vector;
import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @noinspection ALL
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
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
public class VectorizedQuickSortMicrobenchmark {

    public static final int ARRAY_SIZE = 1024 * 1024;

    private int[] inputArray;

    @Setup
    public void setUp() {
//        inputArray = ThreadLocalRandom.current().ints(ARRAY_SIZE).toArray();
        inputArray = IntStream.iterate(0, value -> value + 1)
                .limit(ARRAY_SIZE)
                .toArray();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int[] vectorisedQuickSort() {
        Vector<Integer> one = IntVector.SPECIES_PREFERRED.broadcast(1);
        one.toShuffle();

        VectorizedQuickSort.sort(inputArray);
        return inputArray;
    }

//    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int[] jdkSort() {
        Arrays.sort(inputArray);
//        Baeldung.quickSort(inputArray, 0, inputArray.length - 1);
        return inputArray;
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(VectorizedQuickSortMicrobenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .jvmArgsAppend("--add-modules", "jdk.incubator.vector")
//                .addProfiler(GCProfiler.class)
                .addProfiler(profilerClass)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
