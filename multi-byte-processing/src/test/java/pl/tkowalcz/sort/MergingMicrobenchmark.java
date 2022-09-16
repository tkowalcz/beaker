package pl.tkowalcz.sort;

import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
public class MergingMicrobenchmark {

    private int[] left;
    private int[] right;

    private int[] output;

    @Param("5242880")
    int size;

    @Setup
    public void setUp() {
        left = new Random().ints(0, 1024 * 1024 * 10).limit(size).distinct().toArray();
        right = new Random().ints(1, 1024 * 1024 * 10).limit(size).distinct().toArray();

        Arrays.sort(left);
        Arrays.sort(right);

        output = new int[size + size];
    }

    @Benchmark
    public void simpleMerge() {
        new SortedArraysUnion().simpleUnion(left, right, output);
    }

    @Benchmark
    public void branchlessMerge() {
        new SortedArraysUnion().swarUnion(left, right, output);
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(MergingMicrobenchmark.class.getSimpleName())
                .warmupIterations(4)
                .measurementIterations(4)
                .resultFormat(ResultFormatType.CSV)
//                .addProfiler(profilerClass)
                .threads(1)
                .build();

        new Runner(opt).run();
    }
}
