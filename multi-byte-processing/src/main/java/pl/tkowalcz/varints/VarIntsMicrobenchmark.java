package pl.tkowalcz.varints;

import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.*;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * iMac 2012:
 * Benchmark                           Mode  Cnt  Score   Error  Units
 * VarIntsMicrobenchmark.blogpostLoop  avgt       1.593          us/op
 * VarIntsMicrobenchmark.noLoop        avgt       0.852          us/op
 * VarIntsMicrobenchmark.noLoopAsInt   avgt       0.569          us/op
 * VarIntsMicrobenchmark.simpleLoop    avgt       1.117          us/op
 * <p>
 * AWS (Xeon Platinum 8124m):
 * VarIntsMicrobenchmark.blogpostLoop                        avgt           1.127          us/op
 * VarIntsMicrobenchmark.noLoop                              avgt           0.709          us/op
 * VarIntsMicrobenchmark.noLoopAsInt                         avgt           0.387          us/op
 * VarIntsMicrobenchmark.simpleLoop                          avgt           1.061          us/op
 * <p>
 * Details:
 * Benchmark                                                 Mode  Cnt      Score   Error  Units
 * VarIntsMicrobenchmark.blogpostLoop                        avgt           1.127          us/op
 * VarIntsMicrobenchmark.blogpostLoop:L1-dcache-load-misses  avgt           0.495           #/op
 * VarIntsMicrobenchmark.blogpostLoop:L1-dcache-loads        avgt         924.813           #/op
 * VarIntsMicrobenchmark.blogpostLoop:L1-dcache-stores       avgt        1171.504           #/op
 * VarIntsMicrobenchmark.blogpostLoop:L1-icache-load-misses  avgt           0.608           #/op
 * VarIntsMicrobenchmark.blogpostLoop:branch-misses          avgt           4.182           #/op
 * VarIntsMicrobenchmark.blogpostLoop:branches               avgt        1675.831           #/op
 * VarIntsMicrobenchmark.blogpostLoop:dTLB-load-misses       avgt           0.016           #/op
 * VarIntsMicrobenchmark.blogpostLoop:dTLB-loads             avgt         927.303           #/op
 * VarIntsMicrobenchmark.blogpostLoop:dTLB-store-misses      avgt           0.001           #/op
 * VarIntsMicrobenchmark.blogpostLoop:dTLB-stores            avgt        1168.384           #/op
 * VarIntsMicrobenchmark.blogpostLoop:iTLB-load-misses       avgt           0.002           #/op
 * VarIntsMicrobenchmark.blogpostLoop:iTLB-loads             avgt           0.012           #/op
 * VarIntsMicrobenchmark.blogpostLoop:instructions           avgt       16312.792           #/op
 * VarIntsMicrobenchmark.noLoop                              avgt           0.709          us/op
 * VarIntsMicrobenchmark.noLoop:L1-dcache-load-misses        avgt           0.286           #/op
 * VarIntsMicrobenchmark.noLoop:L1-dcache-loads              avgt         536.651           #/op
 * VarIntsMicrobenchmark.noLoop:L1-dcache-stores             avgt        1555.050           #/op
 * VarIntsMicrobenchmark.noLoop:L1-icache-load-misses        avgt           0.338           #/op
 * VarIntsMicrobenchmark.noLoop:branch-misses                avgt           1.111           #/op
 * VarIntsMicrobenchmark.noLoop:branches                     avgt         650.519           #/op
 * VarIntsMicrobenchmark.noLoop:dTLB-load-misses             avgt           0.008           #/op
 * VarIntsMicrobenchmark.noLoop:dTLB-loads                   avgt         540.757           #/op
 * VarIntsMicrobenchmark.noLoop:dTLB-store-misses            avgt           0.001           #/op
 * VarIntsMicrobenchmark.noLoop:dTLB-stores                  avgt        1551.295           #/op
 * VarIntsMicrobenchmark.noLoop:iTLB-load-misses             avgt           0.002           #/op
 * VarIntsMicrobenchmark.noLoop:iTLB-loads                   avgt           0.004           #/op
 * VarIntsMicrobenchmark.noLoop:instructions                 avgt       10032.523           #/op
 * VarIntsMicrobenchmark.noLoopAsInt                         avgt           0.387          us/op
 * VarIntsMicrobenchmark.noLoopAsInt:L1-dcache-load-misses   avgt           0.146           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:L1-dcache-loads         avgt         406.071           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:L1-dcache-stores        avgt         784.856           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:L1-icache-load-misses   avgt           0.122           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:branch-misses           avgt           1.067           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:branches                avgt         650.803           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:dTLB-load-misses        avgt           0.004           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:dTLB-loads              avgt         406.674           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:dTLB-store-misses       avgt          ? 10??           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:dTLB-stores             avgt         781.630           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:iTLB-load-misses        avgt           0.001           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:iTLB-loads              avgt           0.007           #/op
 * VarIntsMicrobenchmark.noLoopAsInt:instructions            avgt        5715.290           #/op
 * VarIntsMicrobenchmark.simpleLoop                          avgt           1.061          us/op
 * VarIntsMicrobenchmark.simpleLoop:L1-dcache-load-misses    avgt           0.247           #/op
 * VarIntsMicrobenchmark.simpleLoop:L1-dcache-loads          avgt         791.797           #/op
 * VarIntsMicrobenchmark.simpleLoop:L1-dcache-stores         avgt        1171.277           #/op
 * VarIntsMicrobenchmark.simpleLoop:L1-icache-load-misses    avgt           0.649           #/op
 * VarIntsMicrobenchmark.simpleLoop:branch-misses            avgt           4.120           #/op
 * VarIntsMicrobenchmark.simpleLoop:branches                 avgt        2313.134           #/op
 * VarIntsMicrobenchmark.simpleLoop:dTLB-load-misses         avgt           0.011           #/op
 * VarIntsMicrobenchmark.simpleLoop:dTLB-loads               avgt         791.283           #/op
 * VarIntsMicrobenchmark.simpleLoop:dTLB-store-misses        avgt           0.001           #/op
 * VarIntsMicrobenchmark.simpleLoop:dTLB-stores              avgt        1165.860           #/op
 * VarIntsMicrobenchmark.simpleLoop:iTLB-load-misses         avgt           0.001           #/op
 * VarIntsMicrobenchmark.simpleLoop:iTLB-loads               avgt           0.013           #/op
 * VarIntsMicrobenchmark.simpleLoop:instructions             avgt       15814.310           #/op
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
public class VarIntsMicrobenchmark {

    private long[] values;
    private byte[] buffer;

    @Setup
    public void setUp() {
        values = new Random(0)
                .longs(128, 0, Long.MAX_VALUE)
                .toArray();

        buffer = new byte[128 * 10];
    }

    @Benchmark
    public byte[] simpleLoop() {
        int offset = 0;
        for (long value : values) {
            offset = CanonicalVarIntEncoder.encode(buffer, offset, value);
        }

        return buffer;
    }

    @Benchmark
    public byte[] blogpostLoop() {
        int offset = 0;
        for (long value : values) {
            offset = BlogPostVarIntEncoder.encode(buffer, offset, value);
        }

        return buffer;
    }

    @Benchmark
    public byte[] noLoop() {
        int offset = 0;
        for (long value : values) {
            offset = NoLoopVarIntEncoder.encode(buffer, offset, value);
        }

        return buffer;
    }

    @Benchmark
    public byte[] noLoopAsInt() {
        int offset = 0;
        for (long value : values) {
            offset = NoLoop32bitVarIntEncoder.encode(buffer, offset, (int) value);
        }

        return buffer;
    }

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        Class<? extends Profiler> profilerClass = LinuxPerfAsmProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        CommandLineOptions commandLineOptions = new CommandLineOptions(args);
        Options opt = new OptionsBuilder().parent(commandLineOptions)
                .include(VarIntsMicrobenchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(1)
//                .resultFormat(ResultFormatType.CSV)
//                .addProfiler(profilerClass)
//                .addProfiler(GCProfiler.class) // make sure there are no allocations
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
