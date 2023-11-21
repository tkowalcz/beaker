package pl.tkowalcz.lodz;

import jdk.incubator.vector.*;
import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pl.tkowalcz.sort.VectorBubbleSort;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.generate;

/**
 * @noinspection ALL
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1, jvmArgsPrepend = {
        "-XX:+UnlockDiagnosticVMOptions",
        "-XX:+LogVMOutput",
//        "-XX:LoopUnrollLimit=0",
        "-XX:CompileCommand=print,*.selectWhereVectorised_LT",
        "-XX:PrintAssemblyOptions=intel",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+AlwaysPreTouch",
        "-XX:+EnableVectorReboxing",
        "-XX:+EnableVectorAggressiveReboxing",
//        "-XX:+UseEpsilonGC",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
}
)
@Threads(1)
public class SelectWhereMicrobenchmark {

    //    @Param({"0", "25", "50", "75", "100"})
    public int threshold = 50;
    private VectorSpecies<Integer> species;
    private IntVector vectorMask;
    private IntVector notVectorMask;

    private int[] inputArray;

    @Setup
    public void setUp() {
        inputArray = ThreadLocalRandom.current()
                .ints(1024 * 1024 * 256, 0, 100)
                .toArray();

        species = IntVector.SPECIES_PREFERRED;

        int mask = 0x7F_FF_FF_FF;
        int[] vmask = generate(() -> mask).limit(species.length()).toArray();

        vectorMask = IntVector.fromArray(species, vmask, 0);
        notVectorMask = vectorMask.not();

        System.out.println("species = " + species);
    }

    //    @Benchmark
    public int selectWhere() {
        int result = 0;

        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i] > threshold) {
                result++;
            }
        }

        return result;
    }

    @Benchmark
    public int selectWhereVectorised_LT() {
        int result = 0;

        Vector<Integer> thresholdVector = species.broadcast(threshold);

        for (int i = 0; i < species.loopBound(inputArray.length); i += species.length()) {
            IntVector vector = IntVector.fromArray(species, inputArray, i);
            VectorMask<Integer> mask = vector.compare(VectorOperators.GT, thresholdVector);
            result += mask.trueCount();
        }

        return result;
    }

    //    @Benchmark
    public long selectWhereVectorised_Lamport() {
//        long result = 0;

        IntVector thresholdVector = IntVector.broadcast(species, threshold);
        VectorSpecies<Float> floatSpecies = FloatVector.SPECIES_PREFERRED;
        FloatVector result = FloatVector.broadcast(floatSpecies, 0);

        for (int i = 0; i < this.species.loopBound(inputArray.length); i += this.species.length()) {
            IntVector vector = IntVector.fromArray(this.species, inputArray, i);

            vector = vector.lanewise(VectorOperators.XOR, vectorMask)
                    .add(thresholdVector)
                    .lanewise(VectorOperators.AND, notVectorMask)
                    .lanewise(VectorOperators.ADD, vector);

//            result += vector.reduceLanesToLong(VectorOperators.ADD);
            result = result.add(vector.reinterpretAsFloats());

//            result.add(
//                    vector.lanewise(VectorOperators.NOT)
//                            .add(thresholdVector)
//                            .lanewise(VectorOperators.AND, notVectorMask)
//            );
        }

//        return result;
        return result.reduceLanesToLong(VectorOperators.ADD);
    }

    public static void main(String[] args) throws RunnerException {
        Class<? extends Profiler> profilerClass = LinuxPerfProfiler.class;
//        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerClass = DTraceAsmProfiler.class;
        }

        Options opt = new OptionsBuilder()
                .include(SelectWhereMicrobenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .resultFormat(ResultFormatType.CSV)
                .jvmArgsAppend("--add-modules", "jdk.incubator.vector")
                .addProfiler(profilerClass)
//                .addProfiler(JavaFlightRecorderProfiler.class)
//                .threads(3)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
