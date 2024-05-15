package pl.tkowalcz.geecon2024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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
                "-XX:+UnlockDiagnosticVMOptions",
                "-XX:+DebugNonSafepoints",
                "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
        }
)
public class Microbenchmark {

    public static void runWith(String benchmarkName, Class<? extends Profiler> profilerClass, String initLine) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(benchmarkName)
                .forks(notInDebugMode())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .jvmArgsAppend("--add-modules", "jdk.incubator.vector")
                .addProfiler(profilerClass, initLine)
                .build();

        new Runner(opt).run();
    }

    public static void run(String benchmarkName) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(benchmarkName)
                .forks(notInDebugMode())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .jvmArgsAppend("--add-modules", "jdk.incubator.vector")
                .build();

        new Runner(opt).run();
    }

    private static int notInDebugMode() {
        boolean isDebug = ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .toString()
                .contains("-agentlib:jdwp");

        return isDebug ? 0 : 1;
    }

    public static void runWithPerfAsm(String benchmarkName) throws RunnerException {
        runWith(benchmarkName, LinuxPerfAsmProfiler.class, "tooBigThreshold=10000");
    }

    public static void runWithPerfNorm(String benchmarkName) throws RunnerException {
        runWith(benchmarkName, LinuxPerfNormProfiler.class, "");
    }

    public static void runWithPerf(String benchmarkName) throws RunnerException {
        runWith(benchmarkName, LinuxPerfProfiler.class, "");
    }

    public static void runWithGc(String benchmarkName) throws RunnerException {
        runWith(benchmarkName, GCProfiler.class, "");
    }

    public static void runWithJFR(String benchmarkName) throws RunnerException {
        runWith(benchmarkName, JavaFlightRecorderProfiler.class, "");
    }

    public static void runWithAsync(String benchmarkName) throws RunnerException {
        runWith(benchmarkName,
                AsyncProfiler.class,
                "libPath=/root/libasyncProfiler.so;output=flamegraph;dir=profile-results");
    }

    public static double[] randomArrayOfDouble(int arraySize) {
        return IntStream.range(0, arraySize)
                .mapToDouble(value -> (double) value)
                .toArray();
    }

    public static double[] arrayOf(int arraySize) {
        return IntStream.iterate(0, value -> value + 1)
                .limit(arraySize)
                .mapToDouble(value -> (double) value)
                .toArray();
    }

    public static float[] randomArrayOfFloat(int arraySize) {
        float[] result = new float[arraySize];
        for (int i = 0; i < arraySize; i++) {
            result[i] = ThreadLocalRandom.current().nextFloat();
         }

        return result;
    }

    public static int[] randomArrayOfInt(int arraySize) {
        int[] result = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            result[i] = ThreadLocalRandom.current().nextInt(100);
        }

        return result;
    }

    public static long[] randomArrayOfLong(int arraySize) {
        long[] result = new long[arraySize];
        for (int i = 0; i < arraySize; i++) {
            result[i] = ThreadLocalRandom.current().nextLong(100);
        }

        return result;
    }
    public static double[] arrayOf(int arraySize, double valueToSet) {
        return IntStream.range(0, arraySize)
                .mapToDouble(meh -> valueToSet)
                .toArray();
    }
}
