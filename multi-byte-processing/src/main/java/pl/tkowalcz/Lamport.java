//package pl.tkowalcz;
//
//import jdk.incubator.vector.LongVector;
//import jdk.incubator.vector.Vector;
//import jdk.incubator.vector.VectorOperators;
//import org.agrona.BufferUtil;
//import org.agrona.SystemUtil;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.profile.DTraceAsmProfiler;
//import org.openjdk.jmh.profile.GCProfiler;
//import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
//import org.openjdk.jmh.profile.Profiler;
//import org.openjdk.jmh.results.format.ResultFormatType;
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
//import static jdk.incubator.vector.LongVector.*;
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
////        "-XX:+UnlockDiagnosticVMOptions",
////        "-XX:+PrintCompilation",
////        "-XX:-UseSuperWord",
//        "-XX:CompileCommand=print,*.xorVectorized256bit_int",
////        "-XX:CompileCommand=print,*.xorJava",
//        /*"-XX:PrintAssemblyOptions=intel",*/
//        "-XX:+AlwaysPreTouch",
//        "-XX:+UnlockExperimentalVMOptions",
//        "-XX:+UseEpsilonGC",
////        "-XX:+UseVectorApiIntrinsics",
//        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
//}
//)
//public class Lamport {
//
//    private int[] intValues;
//    private ByteBuffer onHeapValues;
//
//    private ByteBuffer onHeapPackedValues;
//    private ByteBuffer offHeapPackedValues;
//    private long[] mask;
//    private long[] predicates;
//
//    long[] results = new long[8 * 4];
//
//    //    @Param({"0", "1", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100"})
//    @Param({"0"})
//    public int predicate;
//
//    int bitWidth = 7;
//    int inputSize = 100 * 1024 * 1024;
//
//    @Setup
//    public void setUp() {
//        intValues = new Random(0)
//                .ints(0, 100)
//                .limit(inputSize)
//                .toArray();
//
//        onHeapValues = ByteBuffer.allocate(inputSize * 4 + 512).order(ByteOrder.nativeOrder());
//        onHeapPackedValues = ByteBuffer.allocate(inputSize * (bitWidth + 1) / 8 + 512).order(ByteOrder.nativeOrder());
////        offHeapPackedValues = ByteBuffer.allocateDirect(inputSize * (bitWidth + 1) / 8 + 512).order(ByteOrder.nativeOrder());
//        offHeapPackedValues = BufferUtil.allocateDirectAligned(inputSize * (bitWidth + 1) / 8 + 512, 1024).order(ByteOrder.nativeOrder());
//
//        Arrays.stream(intValues)
//                .forEach(onHeapValues::putInt);
//
//        BitBuffer onHeapBuffer = new BitBuffer(onHeapPackedValues);
//        BitBuffer offHeapBuffer = new BitBuffer(offHeapPackedValues);
//
//        Arrays.stream(intValues)
//                .forEach(__ -> onHeapBuffer.put(__, bitWidth + 1));
//
//        Arrays.stream(intValues)
//                .forEach(__ -> offHeapBuffer.put(__, bitWidth + 1));
//
//        long mask = 0b0111_1111_0111_1111;
//        mask = (mask << 16) | mask;
//        mask = (mask << 32) | mask;
//
//        long predicate = (this.predicate << 8) | this.predicate;
//        predicate = predicate << 16 | predicate;
//        predicate = predicate << 32 | predicate;
//
//        this.mask = new long[]{mask, mask, mask, mask, mask, mask, mask, mask};
//        this.predicates = new long[]{predicate, predicate, predicate, predicate, predicate, predicate, predicate, predicate};
//    }
//
//    @Benchmark
//    public int plainIf() {
//        int result = 0;
//
//        for (int i = 0; i < inputSize; i++) {
//            if (onHeapValues.getInt(i * 4) < predicate) {
//                result++;
//            }
//        }
//
//        return result;
//    }
//
//    @Benchmark
//    public int plainArithmetic() {
//        int result = 0;
//
//        int mask = 0b0111_1111;
//
//        for (int i = 0; i < inputSize; i++) {
//            int x = onHeapValues.getInt(i * 4) ^ mask;
//            int z = (predicate + x) & ~mask;
//
//            result += Integer.bitCount(z);
//        }
//
//        return result;O
//    }
//
//    @Benchmark
//    public int plainArithmetic_packedByte() {
//        int result = 0;
//
//        int mask = 0b0111_1111;
//
//        for (int i = 0; i < inputSize; i++) {
//            int x = onHeapPackedValues.get(i) ^ mask;
//            int z = (predicate + x) & ~mask;
//
//            result += Integer.bitCount(z);
//        }
//
//        return result;
//    }
//
//    @Benchmark
//    public int plainArithmetic_packedShort() {
//        int result = 0;
//
//        int mask = 0b0111_1111_0111_1111;
//        int predicate = (this.predicate << 8) | this.predicate;
//
//        for (int i = 0; i < inputSize; i += 2) {
//            short aShort = onHeapPackedValues.getShort(i);
//            int x = aShort ^ mask;
//            int z = (predicate + x) & ~mask;
//
//            result += Integer.bitCount(z);
//        }
//
//        return result;
//    }
//
//    @Benchmark
//    public int plainArithmetic_packedInt() {
//        int result = 0;
//
//        int mask = 0b0111_1111_0111_1111;
//        mask = (mask << 16) | mask;
//
//        int predicate = (this.predicate << 8) | this.predicate;
//        predicate = predicate << 16 | predicate;
//
//        for (int i = 0; i < inputSize; i += 4) {
//            int x = onHeapPackedValues.getInt(i) ^ mask;
//            int z = (predicate + x) & ~mask;
//
//            result += Integer.bitCount(z);
//        }
//
//        return result;
//    }
//
//    @Benchmark
//    public int plainArithmetic_packedLong() {
//        int result = 0;
//
//        long mask = 0b0111_1111_0111_1111;
//        mask = (mask << 16) | mask;
//        mask = (mask << 32) | mask;
//
//        long predicate = (this.predicate << 8) | this.predicate;
//        predicate = predicate << 16 | predicate;
//        predicate = predicate << 32 | predicate;
//
//        for (int i = 0; i < inputSize; i += 8) {
//            long x = onHeapPackedValues.getLong(i) ^ mask;
//            long z = (predicate + x) & ~mask;
//
//            result += Long.bitCount(z);
//        }
//
//        return result;
//    }
//
//    // 52428563
//    @Benchmark
//    public int plainArithmetic_packedAVX128() {
//        int bitCount = 0;
//
//        Vector<Long> maskVector = SPECIES_128.fromArray(mask, 0);
//        Vector<Long> notMaskVector = maskVector.neg();
//
//        Vector<Long> predicateVector = SPECIES_128.fromArray(predicates, 0);
//        ByteBuffer source = this.onHeapPackedValues;
////        ByteBuffer source = this.offHeapPackedValues;
//
//        for (int i = 0; i < inputSize; i += 16 * 2) {
//            Vector<Long> x1 = LongVector.fromByteBuffer(SPECIES_128, source, i, ByteOrder.nativeOrder());
//            Vector<Long> x2 = LongVector.fromByteBuffer(SPECIES_128, source, i + 16, ByteOrder.nativeOrder());
//
//            x1 = x1.lanewise(VectorOperators.XOR, maskVector);
//            x2 = x2.lanewise(VectorOperators.XOR, maskVector);
//
//            Vector<Long> z1 = predicateVector.add(x1).lanewise(VectorOperators.AND, notMaskVector);
//            Vector<Long> z2 = predicateVector.add(x2).lanewise(VectorOperators.AND, notMaskVector);
//
////            z1.intoByteBuffer(results, 0, ByteOrder.nativeOrder());
////            z2.intoByteBuffer(results, 16, ByteOrder.nativeOrder());
////
////            bitCount = 0;
////            for (int j = 0; j < z1.species().length(); j++) {
////                bitCount += Long.bitCount(results.getLong(j));
////            }
//        }
//
//        return bitCount;
//    }
//
//    @Benchmark
//    public int plainArithmetic_packedAVX256() {
//        LongVector predicateVector = LongVector.fromArray(SPECIES_256, predicates, 0);
//        LongVector maskVector = LongVector.fromArray(SPECIES_256, mask, 0);
//        LongVector notMaskVector = maskVector.neg();
//
//        int bitCount = 0;
//        for (int i = 0; i < inputSize; i += i += 32) {
//            LongVector.fromByteBuffer(SPECIES_256, onHeapPackedValues, i, ByteOrder.nativeOrder())
//                    .lanewise(VectorOperators.XOR, maskVector)
//                    .add(predicateVector)
//                    .lanewise(VectorOperators.AND, notMaskVector)
//                    .intoArray(results, 0);
//
//            bitCount += Long.bitCount(results[0]) + Long.bitCount(results[1]) + Long.bitCount(results[2]) + Long.bitCount(results[3]);
//        }
//
//        return bitCount;
//    }
//
//    // 52428563
//    @Benchmark
//    public int plainArithmetic_packedAVX256_unrolled() {
//        int bitCount = 0;
//
//        LongVector maskVector = LongVector.fromArray(SPECIES_256, mask, 0);
//        LongVector notMaskVector = maskVector.neg();
//
//        LongVector predicateVector = LongVector.fromArray(SPECIES_256, predicates, 0);
//
//        for (int i = 0; i < inputSize; i += i += 32 * 4) {
//            LongVector x1 = LongVector.fromByteBuffer(SPECIES_256, onHeapPackedValues, i, ByteOrder.nativeOrder());
//            LongVector x2 = LongVector.fromByteBuffer(SPECIES_256, onHeapPackedValues, i + 32, ByteOrder.nativeOrder());
//            LongVector x3 = LongVector.fromByteBuffer(SPECIES_256, onHeapPackedValues, i + 64, ByteOrder.nativeOrder());
//            LongVector x4 = LongVector.fromByteBuffer(SPECIES_256, onHeapPackedValues, i + 96, ByteOrder.nativeOrder());
//
//            x1 = x1.lanewise(VectorOperators.XOR, maskVector);
//            x2 = x2.lanewise(VectorOperators.XOR, maskVector);
//            x3 = x3.lanewise(VectorOperators.XOR, maskVector);
//            x4 = x4.lanewise(VectorOperators.XOR, maskVector);
//
//            LongVector z1 = predicateVector.add(x1).lanewise(VectorOperators.AND, notMaskVector);
//            LongVector z2 = predicateVector.add(x2).lanewise(VectorOperators.AND, notMaskVector);
//            LongVector z3 = predicateVector.add(x3).lanewise(VectorOperators.AND, notMaskVector);
//            LongVector z4 = predicateVector.add(x4).lanewise(VectorOperators.AND, notMaskVector);
//
//            z1.intoArray(results, 0);
//            z2.intoArray(results, 4);
//            z3.intoArray(results, 8);
//            z4.intoArray(results, 12);
//
//            bitCount += Long.bitCount(results[0]) + Long.bitCount(results[1]) + Long.bitCount(results[2]) + Long.bitCount(results[3]);
//            bitCount += Long.bitCount(results[4]) + Long.bitCount(results[5]) + Long.bitCount(results[6]) + Long.bitCount(results[7]);
//            bitCount += Long.bitCount(results[8]) + Long.bitCount(results[9]) + Long.bitCount(results[10]) + Long.bitCount(results[11]);
//            bitCount += Long.bitCount(results[12]) + Long.bitCount(results[13]) + Long.bitCount(results[14]) + Long.bitCount(results[15]);
//        }
//
//        return bitCount;
//    }
//
//    @Benchmark
//    public int plainArithmetic_packedAVX512() {
//        int bitCount = 0;
//
//        Vector<Long> maskVector = SPECIES_512.fromArray(mask, 0);
//        Vector<Long> notMaskVector = maskVector.neg();
//
//        Vector<Long> predicateVector = SPECIES_512.fromArray(predicates, 0);
//
//        for (int i = 0; i < inputSize; i += 64 * 4) {
//            Vector<Long> x1 = LongVector.fromByteBuffer(SPECIES_512, onHeapPackedValues, i, ByteOrder.nativeOrder());
//            Vector<Long> x2 = LongVector.fromByteBuffer(SPECIES_512, onHeapPackedValues, i + 64, ByteOrder.nativeOrder());
//            Vector<Long> x3 = LongVector.fromByteBuffer(SPECIES_512, onHeapPackedValues, i + 128, ByteOrder.nativeOrder());
//            Vector<Long> x4 = LongVector.fromByteBuffer(SPECIES_512, onHeapPackedValues, i + 192, ByteOrder.nativeOrder());
//
//            x1 = x1.lanewise(VectorOperators.XOR, maskVector);
//            x2 = x2.lanewise(VectorOperators.XOR, maskVector);
//            x3 = x3.lanewise(VectorOperators.XOR, maskVector);
//            x4 = x4.lanewise(VectorOperators.XOR, maskVector);
//
//            Vector<Long> z1 = predicateVector.add(x1).lanewise(VectorOperators.AND, notMaskVector);
//            Vector<Long> z2 = predicateVector.add(x2).lanewise(VectorOperators.AND, notMaskVector);
//            Vector<Long> z3 = predicateVector.add(x3).lanewise(VectorOperators.AND, notMaskVector);
//            Vector<Long> z4 = predicateVector.add(x4).lanewise(VectorOperators.AND, notMaskVector);
//
////            z1.intoByteBuffer(results, 0, ByteOrder.nativeOrder());
////            z2.intoByteBuffer(results, 8, ByteOrder.nativeOrder());
////            z3.intoByteBuffer(results, 16, ByteOrder.nativeOrder());
////            z4.intoByteBuffer(results, 24, ByteOrder.nativeOrder());
////
////            bitCount = 0;
////            for (int j = 0; j < z1.species().length(); j++) {
////                bitCount += Long.bitCount(results.getLong(j));
////            }
//        }
//
//        return bitCount;
//    }
//
//    public static void main(String[] args) throws RunnerException {
//        Class<? extends Profiler> profilerClass = LinuxPerfNormProfiler.class;
//        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
//            profilerClass = DTraceAsmProfiler.class;
//        }
//
//        Options opt = new OptionsBuilder()
//                .include(Lamport.class.getSimpleName() + ".plainArithmetic_packedAVX256")
//                .warmupIterations(1)
//                .measurementIterations(1)
//                .resultFormat(ResultFormatType.CSV)
////                .addProfiler(profilerClass)
//                .addProfiler(GCProfiler.class)
//                .build();
//
//        new Runner(opt).run();
//    }
//}
