package pl.tkowalcz;

import net.mintern.primitive.Primitive;
import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Run on i3.xlarge:
 * <p>
 * Model name	: Intel(R) Xeon(R) CPU E5-2686 v4 @ 2.30GHz
 * stepping	: 1
 * microcode	: 0xb000038
 * cpu MHz		: 2700.327
 * cache size	: 46080 KB
 * <p>
 * Benchmark          (dataSize)  Mode  Cnt  Score   Error  Units
 * SmartCopy.copyNew           1  avgt    2  0.009          us/op
 * SmartCopy.copyOld           1  avgt    2  0.010          us/op
 * SmartCopy.copyNew           4  avgt    2  0.013          us/op
 * SmartCopy.copyOld           4  avgt    2  0.013          us/op
 * SmartCopy.copyNew           8  avgt    2  0.005          us/op
 * SmartCopy.copyOld           8  avgt    2  0.014          us/op
 * SmartCopy.copyNew          16  avgt    2  0.016          us/op
 * SmartCopy.copyOld          16  avgt    2  0.032          us/op
 * SmartCopy.copyNew          64  avgt    2  0.024          us/op
 * SmartCopy.copyOld          64  avgt    2  0.039          us/op
 * SmartCopy.copyNew         128  avgt    2  0.029          us/op
 * SmartCopy.copyOld         128  avgt    2  0.045          us/op
 * SmartCopy.copyNew         512  avgt    2  0.062          us/op
 * SmartCopy.copyOld         512  avgt    2  0.081          us/op
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
public class SmartCopy {

    private ByteBuffer source;
    private ByteBuffer destination;

    @Param({"1", "4", "5", "8", "11", "16", "45", "64", "128", "512", "998"})
    public int dataSize;

    @Setup
    public void setUp() {
        byte[] tmp = new byte[dataSize];
        ThreadLocalRandom.current().nextBytes(tmp);

        source = ByteBuffer.wrap(tmp);
        destination = ByteBuffer.allocate(dataSize);
    }

    @Benchmark
    public void copyOld() {
        int i = 0;
        for (; i < source.remaining() - 8; i += 8) {
            destination.putLong(i, source.getLong(i));
        }

        for (; i < source.remaining(); i++) {
            destination.put(i, source.get(i));
        }
    }

    @Benchmark
    public void copyNew() {
        if (source.remaining() < 8) {
            for (int i = 0; i < source.remaining(); i++) {
                destination.put(i, source.get(i));
            }

            return;
        }

        int i = 0;
        for (; i < source.remaining() - 8; i += 8) {
            destination.putLong(i, source.getLong(i));
        }

        destination.putLong(
                destination.remaining() - 8,
                source.getLong(source.remaining() - 8)
        );
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfAsmProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(SmartCopy.class.getSimpleName() + ".*")
                .warmupIterations(2)
                .measurementIterations(2)
                .addProfiler(profilerClass)
                .build();

        new Runner(opt).run();
    }
}
