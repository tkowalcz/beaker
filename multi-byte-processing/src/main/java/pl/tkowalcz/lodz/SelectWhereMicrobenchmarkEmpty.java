package pl.tkowalcz.lodz;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
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

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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
        "-XX:CompileCommand=print,*.selectWhereVectorised_LT",
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
public class SelectWhereMicrobenchmarkEmpty {

    //    @Param({"0", "25", "50", "75", "100"})
    @Param({"50"})
    public int threshold;
    private VectorSpecies<Integer> species;
    private IntVector vectorMask;
    private IntVector notVectorMask;

    private int[] inputArray;

    @Setup
    public void setUp() {
        inputArray = ThreadLocalRandom.current()
                .ints(1024 * 1024 * 256, 0, 100)
                .toArray();

        species = IntVector.SPECIES_PREFERRED;
        System.out.println("species = " + species);
    }

    //    @Benchmark
    public int selectWhere() {
        int result = 0;

        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i] > threshold) {
                result++;
            }
        }

        return result;
    }

    @Benchmark
    public int selectWhereVectorised_LT() {
        int result = 0;

        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfProfiler.class;
//        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(SelectWhereMicrobenchmarkEmpty.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .jvmArgsAppend("--add-modules", "jdk.incubator.vector")
                .addProfiler(profilerClass)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
