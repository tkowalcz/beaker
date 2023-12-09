package pl.tkowalcz;

import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
public class CacheLineDetect {

    private static final int ELEMENTS_COUNT = 1024 * 1024;

    private ByteBuffer clientsDataRowOrder;

    @Param({"4", "8", "16", "32", "48", "64", "80", "96", "112", "128", "144", "160", "176", "192", "208", "224", "240", "256"})
    private int rowSizeBytes;

    @Setup
    public void setUp() {
        Client[] clients = Stream.generate(() -> new Client(rowSizeBytes))
                .limit(ELEMENTS_COUNT)
                .toArray(Client[]::new);

        clientsDataRowOrder = ByteBuffer.allocate(rowSizeBytes * ELEMENTS_COUNT);

        for (Client client : clients) {
            client.encode(clientsDataRowOrder);
        }

        clientsDataRowOrder.flip();
    }

    @Benchmark
    public long sumChristmasShoppingSpend() {
        long result = 0;
        for (int i = 0; i < clientsDataRowOrder.remaining(); i += rowSizeBytes) {
            result += clientsDataRowOrder.getInt(i);
        }

        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(CacheLineDetect.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
//                .addProfiler(profilerClass)
                .build();

        new Runner(opt).run();
    }

    static class Client {

        private final int christmasShoppingSpend;

        private final byte[] moreBusinessCriticalData;

        public Client(int rowWidthBytes) {
            ThreadLocalRandom current = ThreadLocalRandom.current();
            christmasShoppingSpend = current.nextInt();

            rowWidthBytes -= Integer.BYTES;
            moreBusinessCriticalData = new byte[rowWidthBytes];
        }

        public void encode(ByteBuffer buffer) {
            buffer.putInt(christmasShoppingSpend);
            buffer.put(moreBusinessCriticalData);
        }
    }
}
