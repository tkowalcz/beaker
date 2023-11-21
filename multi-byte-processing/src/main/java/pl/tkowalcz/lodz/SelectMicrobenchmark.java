package pl.tkowalcz.lodz;

import jdk.incubator.vector.*;
import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pl.tkowalcz.sort.VectorBubbleSort;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.generate;

/**
 * @noinspection ALL
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1, jvmArgsPrepend = {
        "-XX:+UnlockDiagnosticVMOptions",
        "-XX:+LogVMOutput",
//        "-XX:LoopUnrollLimit=0",
        "-XX:CompileCommand=print,*.selectWhereVector2",
        "-XX:PrintAssemblyOptions=intel",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+AlwaysPreTouch",
        "-XX:+EnableVectorReboxing",
        "-XX:+EnableVectorAggressiveReboxing",
//        "-XX:+UseEpsilonGC",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
}
)
@Threads(1)
public class SelectMicrobenchmark {

    private VectorSpecies<Integer> species;

    private int[] inputArray;

    @Setup
    public void setUp() {
        inputArray = ThreadLocalRandom.current()
                .ints(1024 * 1024 * 256, 0, 100)
                .toArray();

        species = IntVector.SPECIES_PREFERRED;
    }

    //    @Benchmark
    public int selectWhere() {
        int result = 0;

        for (int i = 0; i < inputArray.length; i++) {
            result += inputArray[i];
        }

        return result;
    }

    //    @Benchmark
    public long selectWhereVector() {
        long result = 0;

        for (int i = 0; i < inputArray.length; i += species.length()) {
            IntVector vector = IntVector.fromArray(species, inputArray, i);

            result += vector.reduceLanesToLong(VectorOperators.ADD);
        }

        return result;
    }

    @Benchmark
    public long selectWhereVector2() {
        IntVector result = IntVector.zero(species);

        for (int i = 0; i < species.loopBound(inputArray.length); i += species.length()) {
            IntVector vector = IntVector.fromArray(species, inputArray, i);

            result = result.add(result);
        }

        return result.reduceLanesToLong(VectorOperators.ADD);
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfProfiler.class;
//        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(SelectMicrobenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .jvmArgsAppend("--add-modules", "jdk.incubator.vector")
                .addProfiler(profilerClass)
//                .addProfiler(JavaFlightRecorderProfiler.class)
//                .threads(3)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
