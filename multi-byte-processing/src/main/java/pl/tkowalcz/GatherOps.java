package pl.tkowalcz;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintCompilation",
//        "-XX:-UseSuperWord",
//        "-XX:CompileCommand=print,*.vectorGather_256",
//        "-XX:CompileCommand=print,*.xorJava",
//        "-XX:PrintAssemblyOptions=intel",
//        "-XX:+UseEpsilonGC",
//        "-XX:+UseVectorApiIntrinsics",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+EnableVectorSupport",
        "-XX:+EnableVectorReboxing",
        "-XX:+EnableVectorAggressiveReboxing",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
}
)
public class GatherOps {

    private static final int ELEMENTS_COUNT = 1024;

    private int[] arrayOfStructures;
    private int[] indexMap128;
    private int[] indexMap256;
    private int[] indexMap512;

    private final int size_t = 4;

    @Setup
    public void setUp() {
        arrayOfStructures = new int[size_t * ELEMENTS_COUNT];

        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            for (int j = 0; j < size_t; j++) {
                arrayOfStructures[i * size_t + j] = 1;
            }
        }

        indexMap128 = new int[4];
        for (int i = 0; i < indexMap128.length; i++) {
            indexMap128[i] = i * size_t;
        }

        indexMap256 = new int[8];
        for (int i = 0; i < indexMap256.length; i++) {
            indexMap256[i] = i * size_t;
        }

        indexMap512 = new int[16];
        for (int i = 0; i < indexMap512.length; i++) {
            indexMap512[i] = i * size_t;
        }
    }

    private int gatherAndAddAll(int[] indexMap, VectorSpecies<Integer> species) {
        int result = 0;

        for (int i = 0; i < size_t * ELEMENTS_COUNT; i += indexMap.length * size_t) {
            IntVector vector = IntVector.fromArray(species, arrayOfStructures, i, indexMap, 0);
            result += vector.reduceLanes(VectorOperators.ADD);
        }

        assert result == 1024 : result;
        return result;
    }

    @Benchmark
    public int vectorGather_128() {
        int result = 0;

        IntVector acc = IntVector.zero(IntVector.SPECIES_128);

        for (int i = 0; i < size_t * ELEMENTS_COUNT; i += indexMap128.length * size_t) {
            IntVector vector = IntVector.fromArray(IntVector.SPECIES_128, arrayOfStructures, i, indexMap128, 0);
            acc.add(vector);
        }

        return result;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int vectorGather_256() {
        return gatherAndAddAll(indexMap256, IntVector.SPECIES_256);
    }

    @Benchmark
    public int vectorGather_512() {
        return gatherAndAddAll(indexMap512, IntVector.SPECIES_512);
    }

    @Benchmark
    public int scalarGather() {
        int result = 0;

        for (int i = 0; i < size_t * ELEMENTS_COUNT; i += size_t) {
            result += arrayOfStructures[i];
        }

        assert result == 1024 : result;
        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(GatherOps.class.getSimpleName() + ".vectorGather_128")
                .warmupIterations(10)
                .measurementIterations(10)
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(GCProfiler.class)
//                .addProfiler(profilerClass)
                .build();

        new Runner(opt).run();
    }
}
