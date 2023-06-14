package pl.tkowalcz.sort;

import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
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
public class VectorizedHistogramMicrobenchmark {

    public static final int ARRAY_SIZE = 1024;

    private int[] inputArray;

    private Histogram histogram;
    private VectorizedHistogram vectorizedHistogram;

    @Setup
    public void setUp() {
        inputArray = ThreadLocalRandom.current()
                .ints(ARRAY_SIZE)
                .toArray();

        histogram = new Histogram();
        vectorizedHistogram = new VectorizedHistogram();
    }

    @Benchmark
    public void histogram() {
        histogram.calculate(inputArray);
    }

    @Benchmark
    public void vectorizedHistogram() {
        vectorizedHistogram.calculate(inputArray);
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfAsmProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(VectorizedHistogramMicrobenchmark.class.getSimpleName())
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