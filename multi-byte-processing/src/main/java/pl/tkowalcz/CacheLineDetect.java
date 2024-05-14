package pl.tkowalcz;

import org.agrona.SystemUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
//        "--add-opens java.base/sun.nio.ch=ALL-UNNAMED",
        "-Dagrona.disable.bounds.checks=true",
        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintCompilation",
//        "-XX:CompileCommand=print,*.sumChristmasShoppingSpend"
}
)
public class CacheLineDetect {

    private static final int ELEMENTS_COUNT = 10 * 1024 * 1024;

    private UnsafeBuffer clientsDataRowOrder;

    @Param({"4", "8", "16", "32", "48", "64", "80", "96", "112", "128", "160", "192", "224", "256", "288", "320", "352", "384", "416"})
    private int rowSizeBytes;
    private int index;

    @Setup
    public void setUp() {
        Client[] clients = Stream.generate(() -> new Client(rowSizeBytes))
                .limit(ELEMENTS_COUNT)
                .toArray(Client[]::new);

        clientsDataRowOrder = new UnsafeBuffer(ByteBuffer.allocateDirect(rowSizeBytes * ELEMENTS_COUNT).order(ByteOrder.nativeOrder()));

        index = 0;
        for (Client client : clients) {
            index = client.encode(clientsDataRowOrder, index);
        }
    }

    @Benchmark
    public int sumChristmasShoppingSpend() {
        UnsafeBuffer buffer = this.clientsDataRowOrder;
        int limit = this.index;
        int stride = this.rowSizeBytes;

        int result = 0;
        for (int i = 0; i < limit; i += stride) {
            result += buffer.getInt(i);
        }

        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(CacheLineDetect.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(profilerClass)
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

        public int encode(UnsafeBuffer buffer, int index) {
            buffer.putInt(index, christmasShoppingSpend);
            index += Integer.BYTES;

            buffer.putBytes(index, moreBusinessCriticalData);
            index += moreBusinessCriticalData.length;

            return index;
        }
    }
}
