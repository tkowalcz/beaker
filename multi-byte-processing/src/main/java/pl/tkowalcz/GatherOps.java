package pl.tkowalcz;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.Vector;
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

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintCompilation",
//        "-XX:-UseSuperWord",
//        "-XX:CompileCommand=print,*.vectorGather_256",
//        "-XX:CompileCommand=print,*.xorJava",
        "-XX:PrintAssemblyOptions=intel",
        "-XX:+UnlockExperimentalVMOptions",
//        "-XX:+UseEpsilonGC",
        "-XX:+UseVectorApiIntrinsics",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
}
)
public class GatherOps {

    private static final int ELEMENTS_COUNT = 1024;

    private static final IntVector.IntSpecies SPECIES_128 =
            (IntVector.IntSpecies) Vector.species(int.class, Vector.Shape.S_128_BIT);

    private static final IntVector.IntSpecies SPECIES_256 =
            (IntVector.IntSpecies) Vector.species(int.class, Vector.Shape.S_256_BIT);

    private static final IntVector.IntSpecies SPECIES_512 =
            (IntVector.IntSpecies) Vector.species(int.class, Vector.Shape.S_512_BIT);

    private int[] arrayOfStructures;
    private int[] indexMap128;
    private int[] indexMap256;
    private int[] indexMap512;
    private int size_t = 4;

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

    private int gatherAndAddAll(int[] indexMap, IntVector.IntSpecies species) {
        int result = 0;

        for (int i = 0; i < size_t * ELEMENTS_COUNT; i += indexMap.length * size_t) {
            IntVector vector = species.fromArray(arrayOfStructures, i, indexMap, 0);
            result += vector.addAll();
        }

        assert result == 1024 : result;
        return result;
    }

    @Benchmark
    public int vectorGather_128() {
        return gatherAndAddAll(indexMap128, SPECIES_128);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int vectorGather_256() {
        return gatherAndAddAll(indexMap256, SPECIES_256);
    }

    @Benchmark
    public int vectorGather_512() {
        return gatherAndAddAll(indexMap512, SPECIES_512);
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
                .include(GatherOps.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(1)
                .resultFormat(ResultFormatType.CSV)
                .addProfiler(profilerClass)
                .build();

        new Runner(opt).run();
    }
}
