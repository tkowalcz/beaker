//package pl.tkowalcz;
//
//import jdk.incubator.vector.LongVector;
//import jdk.incubator.vector.VectorOperators;
//import jdk.incubator.vector.VectorSpecies;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.profile.GCProfiler;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.RunnerException;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.Arrays;
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
//import static jdk.incubator.vector.LongVector.SPECIES_128;
//
///**
// * Based on Lamport's "Multiple Byte Processing with FullWord Instructions" (https://lamport.azurewebsites.net/pubs/multiple-byte.pdf)
// * and "BitWeaving: Fast Scans for Main Memory Data Processing" http://pages.cs.wisc.edu/~jignesh/publ/BitWeaving.pdf by
// * Yinan Li and Jignesh M. Patel.
// */
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Benchmark)
//@Fork(value = 1, jvmArgsPrepend = {
//        "-Xmx16g",
//        "-XX:+AlwaysPreTouch",
//        "-XX:+UnlockExperimentalVMOptions",
//        "-XX:+UseEpsilonGC",
//        "-XX:+UnlockDiagnosticVMOptions",
////        "-XX:CompileCommand=print,*.packedAVX256",
////        "-XX:+LogCompilation",
////        "-XX:+PrintCompilation",
////        "-XX:MaxInlineSize=150",
////        "-XX:+PrintInlining",
//        "-XX:+DebugNonSafepoints",
////        "-XX:+PrintEscapeAnalysis",
////        "-XX:+PrintEliminateAllocations",
//        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
//}
//)
//public class Lamport256 {
//
//    private ByteBuffer onHeapPackedValues;
//    private long[] mask;
//    private long[] predicates;
//    private long[] results;
//
//    //    @Param({"0", "1", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100"})
//    @Param({"0"})
//    public int predicate;
//
//    int bitWidth = 7;
//    int inputSize = 100 * 1024 * 1024;
//    private LongVector predicateVector;
//    private LongVector maskVector;
//    private LongVector notMaskVector;
//
//    @Setup
//    public void setUp() {
//        results = new long[8 * 4];
//
//        int[] intValues = new Random(0)
//                .ints(0, 100)
//                .limit(inputSize)
//                .toArray();
//
//        onHeapPackedValues = ByteBuffer.allocate(inputSize * (bitWidth + 1) / 8 + 512).order(ByteOrder.nativeOrder());
//        BitBuffer onHeapBuffer = new BitBuffer(onHeapPackedValues);
//
//        Arrays.stream(intValues)
//                .forEach(__ -> onHeapBuffer.put(__, bitWidth + 1));
//
//        long mask = 0b0111_1111_0111_1111;
//        mask = (mask << 16) | mask;
//        mask = (mask << 32) | mask;
//
//        long predicate = ((long) this.predicate << 8) | this.predicate;
//        predicate = predicate << 16 | predicate;
//        predicate = predicate << 32 | predicate;
//
//        this.mask = new long[]{mask, mask, mask, mask, mask, mask, mask, mask};
//        this.predicates = new long[]{predicate, predicate, predicate, predicate, predicate, predicate, predicate, predicate};
//
//        predicateVector = LongVector.fromArray(SPECIES_128, predicates, 0);
//        maskVector = LongVector.fromArray(SPECIES_128, this.mask, 0);
//        notMaskVector = maskVector.neg();
//    }
//
//    @Benchmark
//    public int packedAVX256() {
//        VectorSpecies<Long> preferredSpecies = LongVector.SPECIES_PREFERRED;
//
//        int bitCount = 0;
//        for (int i = 0; i < preferredSpecies.loopBound(inputSize); i += preferredSpecies.vectorByteSize()) {
//            LongVector.fromByteBuffer(preferredSpecies, onHeapPackedValues, i, ByteOrder.nativeOrder())
//                    .lanewise(VectorOperators.XOR, maskVector)
//                    .add(predicateVector)
//                    .lanewise(VectorOperators.AND, notMaskVector)
//                    .intoArray(results, 0);
//
//            bitCount += Long.bitCount(results[0]) + Long.bitCount(results[1]);
//        }
//
//        return bitCount;
//    }
//
//    public static void main(String[] args) throws RunnerException {
//        Options opt = new OptionsBuilder()
//                .include(Lamport256.class.getSimpleName())
//                .warmupIterations(2)
//                .measurementIterations(2)
//                .addProfiler(GCProfiler.class)
//                .build();
//
//        new Runner(opt).run();
//    }
//}
