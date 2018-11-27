package pl.tkowalcz;

import org.agrona.SystemUtil;
import org.agrona.UnsafeAccess;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.misc.Unsafe;
import xerial.jnuma.Numa;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.agrona.BitUtil.isPowerOfTwo;
import static org.agrona.BufferUtil.address;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class AllCorePingPongMicrobenchmark {

    private static final Unsafe UNSAFE = UnsafeAccess.UNSAFE;

    @State(Scope.Benchmark)
    public static class CoreSequence {

        @Param("0,0")
        public String coresConfigurations;

        List<Integer> cores;
        Core2CoreDescriptor core2CoreDescriptor;

        @Setup
        public void setUp() {
            core2CoreDescriptor = Core2CoreDescriptor.fromString(coresConfigurations);
            cores = core2CoreDescriptor.toSynchronizedList();

            System.out.println("Will test following cores configuration: " + cores);
        }
    }

    @State(Scope.Group)
    public static class GroupState {

        private long flagAddress;

        @Setup
        public void setUp(CoreSequence coreSequence) {
            flagAddress = allocateAligned(4096, 1024, coreSequence.core2CoreDescriptor.getCore1());
        }
    }

    @State(Scope.Thread)
    public static class CoreAssigner {

        @Setup
        public void setUp(CoreSequence coreSequence) {
            Integer core = coreSequence.cores.remove(0);

            Numa.runOnNode(core);
            System.out.println("Running on node: " + core);
        }
    }

    @Benchmark
    @Group("pingpong")
    public void ping(Control cnt, GroupState groupState, CoreAssigner coreAssigner) {
        while (!cnt.stopMeasurement && !UNSAFE.compareAndSwapInt(null, groupState.flagAddress, 0, 1)) {
            // this body is intentionally left blank
        }
    }

    @Benchmark
    @Group("pingpong")
    public void pong(Control cnt, GroupState groupState, CoreAssigner coreAssigner) {
        while (!cnt.stopMeasurement && !UNSAFE.compareAndSwapInt(null, groupState.flagAddress, 1, 0)) {
            // this body is intentionally left blank
        }
    }

    public static void main(String[] args) throws RunnerException {
        String[] coresConfigurations = IntStream.range(0, Numa.numNodes())
                .boxed()
                .flatMap(i -> IntStream.range(0, Numa.numNodes())
                        .mapToObj(__ -> new Core2CoreDescriptor(__, i)))
                .map(Core2CoreDescriptor::toString)
                .toArray(String[]::new);

        Class<? extends Profiler> profilerClass = LinuxPerfAsmProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        System.out.println("Will test following cores configurations: " + Arrays.toString(coresConfigurations));

        Options opt = new OptionsBuilder()
                .include(AllCorePingPongMicrobenchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(1)
                .param("coresConfigurations", coresConfigurations)
                .jvmArgs(
                        "-XX:+UnlockExperimentalVMOptions",
                        "-XX:+UseEpsilonGC"
                )
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(profilerClass)
                .threads(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    // Based on org.agrona.BufferUtil::allocateDirectAligned
    public static long allocateAligned(final int capacity, final int alignment, final int node) {
        if (!isPowerOfTwo(alignment)) {
            throw new IllegalArgumentException("Must be a power of 2: alignment=" + alignment);
        }

        ByteBuffer buffer;
        if (Numa.isAvailable()) {
            buffer = Numa.allocOnNode(capacity + alignment, node);
        } else {
            buffer = ByteBuffer.allocateDirect(capacity + alignment);
        }

        final long address = address(buffer);
        final int remainder = (int) (address & (alignment - 1));
        final int offset = alignment - remainder;

        return address + offset;
    }
}

class Core2CoreDescriptor {

    private int core1;
    private int core2;

    public Core2CoreDescriptor(int core1, int core2) {
        this.core1 = core1;
        this.core2 = core2;
    }

    public int getCore1() {
        return core1;
    }

    public int getCore2() {
        return core2;
    }

    @Override
    public String toString() {
        return core1 + "," + core2;
    }

    public List<Integer> toSynchronizedList() {
        return Collections.synchronizedList(new ArrayList<>(List.of(core1, core2)));
    }

    public static Core2CoreDescriptor fromString(String coresConfigurations) {
        String[] split = coresConfigurations.split(",");
        return new Core2CoreDescriptor(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
