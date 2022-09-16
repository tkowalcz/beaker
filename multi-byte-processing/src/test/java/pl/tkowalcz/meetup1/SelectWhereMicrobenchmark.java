package pl.tkowalcz.meetup1;

import com.google.common.io.Files;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintCompilation",
//        "-XX:-UseSuperWord",
        "-XX:CompileCommand=print,*.vectorizedLoop",
//        "-XX:CompileCommand=print,*.xorJava",
        "-XX:PrintAssemblyOptions=intel",
//        "-XX:+UseEpsilonGC",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+EnableVectorSupport",
        "-XX:+EnableVectorReboxing",
        "-XX:+EnableVectorAggressiveReboxing",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
})
public class SelectWhereMicrobenchmark {

    @Param({"rows_5.dat", "rows_25.dat", "rows_50.dat"})
    private String file;
    private byte[] data;

    @Setup
    public void setUp() {
        data = new byte[0];
        try {
            data = Files.toByteArray(new File(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Benchmark
    public int plainLoop() {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int below = 0;
        while (buffer.hasRemaining()) {
            int id = buffer.getInt();
            int value = buffer.getInt();

            if (value < 150) {
                below++;
            }
        }

        return below;
    }

    //    @Benchmark
    public int vectorizedLoop() {
        MemorySegment memorySegment = MemorySegment.ofArray(data);
        VectorSpecies<Integer> species = VectorSpecies.ofPreferred(int.class);
        ByteOrder nativeOrder = ByteOrder.nativeOrder();

        VectorMask<Integer> mask = VectorMask.fromValues(species, false, true, false, true);

        int below = 0;
        for (int i = 0; i < data.length; i += species.vectorByteSize()) {
            IntVector intVector = IntVector.fromMemorySegment(
                    species,
                    memorySegment,
                    i,
                    nativeOrder
            );

            VectorMask<Integer> compare = intVector.compare(VectorOperators.LT, 150, mask);
            below += compare.trueCount();
        }

        return below;
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(SelectWhereMicrobenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(profilerClass)
                .threads(1)
                .build();

        new Runner(opt).run();
    }
}
