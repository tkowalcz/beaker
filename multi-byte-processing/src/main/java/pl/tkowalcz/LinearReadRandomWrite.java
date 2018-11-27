package pl.tkowalcz;

import net.mintern.primitive.Primitive;
import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:-UseSuperWord",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseEpsilonGC",
        "-XX:+UseVectorApiIntrinsics",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
}
)
public class LinearReadRandomWrite {

    private double[] sampledArray;
    private double[] outputArray;

    private int[] randomArray;
    private int[] randomPrecomputedArray;
    private long[] randomPrecomputedSotedArray;

    @Param({"100000000"})
    public int sampledArraySize;

    @Param({"100000"})
    public int outputArraySize;

    @Setup
    public void setUp() {
        sampledArray = new Random(0)
                .doubles()
                .limit(sampledArraySize)
                .toArray();

        outputArray = new double[outputArraySize];
        randomArray = new int[outputArraySize];
        randomPrecomputedArray = new int[outputArraySize];
        randomPrecomputedSotedArray = new long[outputArraySize];

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < outputArraySize; i++) {
            randomPrecomputedArray[i] = random.nextInt(sampledArraySize);
            randomPrecomputedSotedArray[i] = ((long) i << 32L) + random.nextInt(sampledArraySize);
        }

        Primitive.sort(randomPrecomputedSotedArray, (l1, l2) -> Integer.compare((int) l1, (int) l2));
    }

    @Benchmark
    public int[] randomBaseline() {
        int length = outputArraySize;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            randomArray[i] = random.nextInt(sampledArraySize);
        }

        return randomArray;
    }

    @Benchmark
    public double[] baseCase() {
        int length = outputArraySize;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(sampledArraySize);
            double sample = sampledArray[index];

            outputArray[i] = sample;
        }

        return outputArray;
    }

    @Benchmark
    public double[] randomArray() {
        int length = outputArraySize;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            randomArray[i] = random.nextInt(sampledArraySize);
        }

        for (int i = 0; i < length; i++) {
            int index = randomArray[i];
            double sample = sampledArray[index];
            outputArray[i] = sample;
        }

        return outputArray;
    }

    @Benchmark
    public double[] randomPrecomputedArray() {
        int length = outputArraySize;

        for (int i = 0; i < length; i++) {
            int index = randomPrecomputedArray[i];
            double sample = sampledArray[index];
            outputArray[i] = sample;
        }

        return outputArray;
    }

    @Benchmark
    public double[] randomPrecomputedSortedArray() {
        int length = outputArraySize;

        for (int i = 0; i < length; i++) {
            long value = randomPrecomputedSotedArray[i];
            int sourceIndex = (int) value;
            int destinationIndex = (int) (value >> 32);

            double sample = sampledArray[sourceIndex];
            outputArray[destinationIndex] = sample;
        }

        return outputArray;
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfAsmProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(LinearReadRandomWrite.class.getSimpleName() + ".randomPrecomputedSortedArray")
                .warmupIterations(1)
                .measurementIterations(1)
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(profilerClass)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
