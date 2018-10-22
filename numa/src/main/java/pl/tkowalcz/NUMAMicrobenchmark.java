package pl.tkowalcz;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xerial.jnuma.Numa;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class NUMAMicrobenchmark {

    private final Random random = new Random(0);

    // Discovered on startup and filled with list of all numa nodes
    @Param("0")
    int threadNumaNode;

    // Discovered on startup and filled with list of all numa nodes
    @Param("0")
    int dataNumaNode;

    @Param("100")
    int dataSizeMegabytes;

    private ByteBuffer byteBuffer;

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.OPERATIONS)
    public static class Throughput {

        private long bytesProcessed;

        public long throughputBytes() {
            return bytesProcessed;
        }

        void increment(int remaining) {
            bytesProcessed += remaining;
        }
    }

    @Setup(Level.Iteration)
    public void setUp() {
        byteBuffer = Numa.allocOnNode(dataSizeMegabytes * 1024 * 1024, dataNumaNode);

        for (int i = 0; i < byteBuffer.remaining(); i++) {
            byteBuffer.put((byte) 0);
        }

        byteBuffer.flip();
    }

    @TearDown
    public void tearDown() {
        Numa.free(byteBuffer);
    }

    @Benchmark
    public int readByteBufferInOrder(Throughput throughput) {
        Numa.runOnNode(threadNumaNode);

        int result = 0;
        for (int i = 0; i < byteBuffer.limit(); i += 8) {
            result += byteBuffer.getLong(i);
        }

        throughput.increment(byteBuffer.remaining());
        return result;
    }

    @Benchmark
    public int readByteBufferRandomly(Throughput throughput) {
        Numa.runOnNode(threadNumaNode);

        int result = 0;
        int limitBytes = byteBuffer.limit();
        int limitElements = limitBytes / 8;

        for (int i = 0; i < limitBytes; i += 8) {
            int index = random.nextInt(limitElements);
            result += byteBuffer.getLong(index * 8);
        }

        throughput.increment(byteBuffer.remaining());
        return result;
    }

    // TODO: verify that this does not get optimised away
    @Benchmark
    public void writeByteBufferInOrder(Throughput throughput) {
        Numa.runOnNode(threadNumaNode);

        for (int i = 0; i < byteBuffer.limit(); i += 8) {
            byteBuffer.putLong(i, 0xABCDEF_ABCDEFL);
        }

        throughput.increment(byteBuffer.remaining());
    }

    // TODO: verify that this does not get optimised away
    @Benchmark
    public void writeByteBufferRandomly(Throughput throughput) {
        Numa.runOnNode(threadNumaNode);

        int limitBytes = byteBuffer.limit();
        int limitElements = limitBytes / 8;

        for (int i = 0; i < limitBytes; i += 8) {
            int index = random.nextInt(limitElements);
            byteBuffer.putLong(index * 8, 0xABCDEF_ABCDEFL);
        }

        throughput.increment(byteBuffer.remaining());
    }

    public static void main(String[] args) throws RunnerException {
        assert Numa.isAvailable() : "Numa is not available";

        printNumaSettings();

        Options opt = new OptionsBuilder()
                .include(".*" + NUMAMicrobenchmark.class.getSimpleName() + ".*")
                .warmupIterations(1)
                .measurementIterations(1)
                .jvmArgs("-XX:+UnlockExperimentalVMOptions", "-XX:+UseEpsilonGC")
                .param("threadNumaNode", range(0, Numa.numNodes()))
                .param("dataNumaNode", range(0, Numa.numNodes()))
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private static void printNumaSettings() {
        int numNodes = Numa.numNodes();
        System.out.println("NUMA topology");
        System.out.println("nodes: " + numNodes);
        for (int i = 0; i < numNodes; i++) {
            long nodeSize = Numa.nodeSize(i);
            System.out.println("node " + i + " size = " + (nodeSize / 1024 / 1024) + "mb");

            long[] longs = Numa.nodeToCpus(i);
            String bitset = BitSet.valueOf(longs).toString();
            System.out.println(bitset);
        }

        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                int distance = Numa.distance(i, j);
                System.out.println("Distance (" + i + "," + j + "): " + distance);
            }
        }
    }

    private static String[] range(int fromInclusive, int toExclusive) {
        return IntStream.range(fromInclusive, toExclusive)
                .mapToObj(Integer::toString)
                .toArray(String[]::new);
    }
}
