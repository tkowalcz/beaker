package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @noinspection ALL
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+LogVMOutput",
//        "-XX:CompileCommand=print,*.bitonicSort",
//        "-XX:PrintAssemblyOptions=intel",
//        "-XX:+UnlockExperimentalVMOptions",
//        "-XX:+UseEpsilonGC",
//        "-XX:+UseVectorApiIntrinsics",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
}
)
public class VectorSortMicrobenchmark {

    private VectorBubbleSort bubbleSort = new VectorBubbleSort();

    private int[] inputArray;
    private int[] dataArray;

    @Setup
    public void setUp() {
        inputArray = new int[]{42, 33, 56, 76, 3, 89, 124, 22};
        dataArray = new int[inputArray.length];
    }

    @Benchmark
    public int[] arraySort() {
        System.arraycopy(inputArray, 0, dataArray, 0, inputArray.length);

        Arrays.sort(dataArray);
        return dataArray;
    }

    @Benchmark
    public void bubbleSort() {
        IntVector input = IntVector.fromArray(VectorBubbleSort.SPECIES_I256, inputArray, 0);
        IntVector sorted = bubbleSort.sort(input);

        sorted.intoArray(dataArray, 0);
    }

    @Benchmark
    public void bitonicSort() {
        IntVector input = IntVector.fromArray(SingleVectorBitonicSort.SPECIES_I256, inputArray, 0);
        IntVector sorted = SingleVectorBitonicSort.sort(input);

        sorted.intoArray(dataArray, 0);
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(VectorSortMicrobenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(GCProfiler.class)
//                .addProfiler(profilerClass)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
