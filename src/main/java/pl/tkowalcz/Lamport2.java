package pl.tkowalcz;

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.Shapes;
import jdk.incubator.vector.Vector;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Based on Lamport's "Multiple Byte Processing with FullWord Instructions" (https://lamport.azurewebsites.net/pubs/multiple-byte.pdf)
 * and "BitWeaving: Fast Scans for Main Memory Data Processing" http://pages.cs.wisc.edu/~jignesh/publ/BitWeaving.pdf by
 * Yinan Li and Jignesh M. Patel.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintCompilation",
//        "-XX:-UseSuperWord",
//        "-XX:CompileCommand=print,*.xorVectorized256bit_int",
//        "-XX:CompileCommand=print,*.xorJava",
        /*"-XX:PrintAssemblyOptions=intel",*/
        "-XX:-UseCompressedOops",
        "-XX:+UseVectorApiIntrinsics",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
}
)
public class Lamport2 {

    private int[] intValues;
    private ByteBuffer onHeapValues;

    private ByteBuffer onHeapPackedValues;
    private ByteBuffer offHeapPackedValues;
    private long[] mask;
    private long[] predicates;

    long[] results = new long[8 * 4];

    private static final LongVector.LongSpecies<Shapes.S128Bit> SPECIES_128 =
            (LongVector.LongSpecies<Shapes.S128Bit>) Vector.species(long.class, Shapes.S_128_BIT);

    private static final LongVector.LongSpecies<Shapes.S256Bit> SPECIES_256 =
            (LongVector.LongSpecies<Shapes.S256Bit>) Vector.species(long.class, Shapes.S_256_BIT);

    private static final LongVector.LongSpecies<Shapes.S512Bit> SPECIES_512 =
            (LongVector.LongSpecies<Shapes.S512Bit>) Vector.species(long.class, Shapes.S_512_BIT);

    //    @Param({"0", "1", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100"})
    @Param({"50"})
    public int predicate;

    int bitWidth = 7;
    int inputSize = 100 * 1024 * 1024;

    @Setup
    public void setUp() {
        intValues = new Random(0)
                .ints(0, 100)
                .limit(inputSize)
                .toArray();

        onHeapValues = ByteBuffer.allocate(inputSize * 4 + 512).order(ByteOrder.nativeOrder());
        onHeapPackedValues = ByteBuffer.allocate(inputSize * (bitWidth + 1) / 8 + 512).order(ByteOrder.nativeOrder());
        offHeapPackedValues = ByteBuffer.allocateDirect(inputSize * (bitWidth + 1) / 8 + 512).order(ByteOrder.nativeOrder());

        Arrays.stream(intValues)
                .forEach(onHeapValues::putInt);

        BitBuffer onHeapBuffer = new BitBuffer(onHeapPackedValues);
        BitBuffer offHeapBuffer = new BitBuffer(offHeapPackedValues);

        Arrays.stream(intValues)
                .forEach(__ -> onHeapBuffer.put(__, bitWidth + 1));

        Arrays.stream(intValues)
                .forEach(__ -> offHeapBuffer.put(__, bitWidth + 1));

        long mask = 0b0111_1111_0111_1111;
        mask = (mask << 16) | mask;
        mask = (mask << 32) | mask;

        long predicate = (this.predicate << 8) | this.predicate;
        predicate = predicate << 16 | predicate;
        predicate = predicate << 32 | predicate;

        this.mask = new long[]{mask, mask, mask, mask, mask, mask, mask, mask};
        this.predicates = new long[]{predicate, predicate, predicate, predicate, predicate, predicate, predicate, predicate};
    }

    // 52428563
    @Benchmark
    public int plainIf() {
        int result = 0;

        for (int i = 0; i < inputSize; i++) {
            if (onHeapValues.getInt(i * 4) < predicate) {
                result++;
            }
        }

//        System.out.println("result = " + result);
        return result;
    }

//    @Benchmark
//    public int bandwidth() {
//        int result = 0;
//
//        for (int i = 0; i < inputSize; i++) {
//            result += onHeapValues.getInt(i);
//        }
//
//        return result;
//    }

    // 52428563
    @Benchmark
    public int plainArithmetic() {
        int result = 0;

        int mask = 0b0111_1111;

        for (int i = 0; i < inputSize; i++) {
            int x = onHeapValues.getInt(i * 4) ^ mask;
            int z = (predicate + x) & ~mask;

            result += Integer.bitCount(z);
        }

//        System.out.println("result = " + result);
        return result;
    }

    // 52428563
    @Benchmark
    public int plainArithmetic_packedByte() {
        int result = 0;

        int mask = 0b0111_1111;

        for (int i = 0; i < inputSize; i++) {
            int x = onHeapPackedValues.get(i) ^ mask;
            int z = (predicate + x) & ~mask;

            result += Integer.bitCount(z);
        }

//        System.out.println("result = " + result);
        return result;
    }

    // 52428563
    @Benchmark
    public int plainArithmetic_packedShort() {
        int result = 0;

        int mask = 0b0111_1111_0111_1111;
        int predicate = (this.predicate << 8) | this.predicate;

        for (int i = 0; i < inputSize; i += 2) {
            short aShort = onHeapPackedValues.getShort(i);
            int x = aShort ^ mask;
            int z = (predicate + x) & ~mask;

            result += Integer.bitCount(z);
        }

//        System.out.println("result = " + result);
        return result;
    }

    // 52428563
    @Benchmark
    public int plainArithmetic_packedInt() {
        int result = 0;

        int mask = 0b0111_1111_0111_1111;
        mask = (mask << 16) | mask;

        int predicate = (this.predicate << 8) | this.predicate;
        predicate = predicate << 16 | predicate;

        for (int i = 0; i < inputSize; i += 4) {
            int x = onHeapPackedValues.getInt(i) ^ mask;
            int z = (predicate + x) & ~mask;

            result += Integer.bitCount(z);
        }

//        System.out.println("result = " + result);
        return result;
    }

    // 52428563
    @Benchmark
    public int plainArithmetic_packedLong() {
        int result = 0;

        long mask = 0b0111_1111_0111_1111;
        mask = (mask << 16) | mask;
        mask = (mask << 32) | mask;

        long predicate = (this.predicate << 8) | this.predicate;
        predicate = predicate << 16 | predicate;
        predicate = predicate << 32 | predicate;

        for (int i = 0; i < inputSize; i += 8) {
            long x = onHeapPackedValues.getLong(i) ^ mask;
            long z = (predicate + x) & ~mask;

            result += Long.bitCount(z);
        }

//        System.out.println("result = " + result);
        return result;
    }

    // 52428563
    @Benchmark
    public int plainArithmetic_packedAVX128() {
        int result = 0;

//        long mask = 0b0111_1111_0111_1111;
//        mask = (mask << 16) | mask;
//        mask = (mask << 32) | mask;
//
//        long predicate = (this.predicate << 8) | this.predicate;
//        predicate = predicate << 16 | predicate;
//        predicate = predicate << 32 | predicate;

        LongVector<Shapes.S128Bit> maskVector = SPECIES_128.fromArray(mask, 0);
        LongVector<Shapes.S128Bit> notMaskVector = maskVector.not();

        LongVector<Shapes.S128Bit> predicateVector = SPECIES_128.fromArray(predicates, 0);
        ByteBuffer source = this.onHeapPackedValues;
//        ByteBuffer source = this.offHeapPackedValues;

        for (int i = 0; i < inputSize; i += 16 * 4) {
            LongVector<Shapes.S128Bit> x1 = SPECIES_128.fromByteBuffer(source, i);
            LongVector<Shapes.S128Bit> x2 = SPECIES_128.fromByteBuffer(source, i + 16);
            LongVector<Shapes.S128Bit> x3 = SPECIES_128.fromByteBuffer(source, i + 32);
            LongVector<Shapes.S128Bit> x4 = SPECIES_128.fromByteBuffer(source, i + 48);

            x1 = x1.xor(maskVector);
            x2 = x2.xor(maskVector);
            x3 = x3.xor(maskVector);
            x4 = x4.xor(maskVector);

            LongVector<Shapes.S128Bit> z1 = predicateVector.add(x1).and(notMaskVector);
            LongVector<Shapes.S128Bit> z2 = predicateVector.add(x2).and(notMaskVector);
            LongVector<Shapes.S128Bit> z3 = predicateVector.add(x3).and(notMaskVector);
            LongVector<Shapes.S128Bit> z4 = predicateVector.add(x4).and(notMaskVector);

            z1.intoArray(results, 0);
            z2.intoArray(results, 2);
            z3.intoArray(results, 4);
            z4.intoArray(results, 6);

            result += Long.bitCount(results[0]) + Long.bitCount(results[1]) + Long.bitCount(results[2]) + Long.bitCount(results[3]);
            result += Long.bitCount(results[4]) + Long.bitCount(results[5]) + Long.bitCount(results[6]) + Long.bitCount(results[7]);
        }

//        System.out.println("result = " + result);
        return result;
    }

    // 52428563
    @Benchmark
    public int plainArithmetic_packedAVX256() {
        int result = 0;

//        long mask = 0b0111_1111_0111_1111;
//        mask = (mask << 16) | mask;
//        mask = (mask << 32) | mask;
//
//        long predicate = (this.predicate << 8) | this.predicate;
//        predicate = predicate << 16 | predicate;
//        predicate = predicate << 32 | predicate;

        LongVector<Shapes.S256Bit> maskVector = SPECIES_256.fromArray(mask, 0);
        LongVector<Shapes.S256Bit> notMaskVector = maskVector.not();

        LongVector<Shapes.S256Bit> predicateVector = SPECIES_256.fromArray(predicates, 0);

        for (int i = 0; i < inputSize; i += 32 * 4) {
            LongVector<Shapes.S256Bit> x1 = SPECIES_256.fromByteBuffer(onHeapPackedValues, i);
            LongVector<Shapes.S256Bit> x2 = SPECIES_256.fromByteBuffer(onHeapPackedValues, i + 32);
            LongVector<Shapes.S256Bit> x3 = SPECIES_256.fromByteBuffer(onHeapPackedValues, i + 64);
            LongVector<Shapes.S256Bit> x4 = SPECIES_256.fromByteBuffer(onHeapPackedValues, i + 96);

            x1 = x1.xor(maskVector);
            x2 = x2.xor(maskVector);
            x3 = x3.xor(maskVector);
            x4 = x4.xor(maskVector);

            LongVector<Shapes.S256Bit> z1 = predicateVector.add(x1).and(notMaskVector);
            LongVector<Shapes.S256Bit> z2 = predicateVector.add(x2).and(notMaskVector);
            LongVector<Shapes.S256Bit> z3 = predicateVector.add(x3).and(notMaskVector);
            LongVector<Shapes.S256Bit> z4 = predicateVector.add(x4).and(notMaskVector);

            z1.intoArray(results, 0);
            z2.intoArray(results, 4);
            z3.intoArray(results, 8);
            z4.intoArray(results, 12);

            result += Long.bitCount(results[0]) + Long.bitCount(results[1]) + Long.bitCount(results[2]) + Long.bitCount(results[3]);
            result += Long.bitCount(results[4]) + Long.bitCount(results[5]) + Long.bitCount(results[6]) + Long.bitCount(results[7]);
            result += Long.bitCount(results[8]) + Long.bitCount(results[9]) + Long.bitCount(results[10]) + Long.bitCount(results[11]);
            result += Long.bitCount(results[12]) + Long.bitCount(results[13]) + Long.bitCount(results[14]) + Long.bitCount(results[15]);
        }

//        System.out.println("result = " + result);
        return result;
    }

    @Benchmark
    public int plainArithmetic_packedAVX512() {
        int result = 0;

//        long mask = 0b0111_1111_0111_1111;
//        mask = (mask << 16) | mask;
//        mask = (mask << 32) | mask;
//
//        long predicate = (this.predicate << 8) | this.predicate;
//        predicate = predicate << 16 | predicate;
//        predicate = predicate << 32 | predicate;

        LongVector<Shapes.S512Bit> maskVector = SPECIES_512.fromArray(mask, 0);
        LongVector<Shapes.S512Bit> notMaskVector = maskVector.not();

        LongVector<Shapes.S512Bit> predicateVector = SPECIES_512.fromArray(predicates, 0);

        for (int i = 0; i < inputSize; i += 64 * 4) {
            LongVector<Shapes.S512Bit> x1 = SPECIES_512.fromByteBuffer(onHeapPackedValues, i);
            LongVector<Shapes.S512Bit> x2 = SPECIES_512.fromByteBuffer(onHeapPackedValues, i + 64);
            LongVector<Shapes.S512Bit> x3 = SPECIES_512.fromByteBuffer(onHeapPackedValues, i + 128);
            LongVector<Shapes.S512Bit> x4 = SPECIES_512.fromByteBuffer(onHeapPackedValues, i + 192);

            x1 = x1.xor(maskVector);
            x2 = x2.xor(maskVector);
            x3 = x3.xor(maskVector);
            x4 = x4.xor(maskVector);

            LongVector<Shapes.S512Bit> z1 = predicateVector.add(x1).and(notMaskVector);
            LongVector<Shapes.S512Bit> z2 = predicateVector.add(x2).and(notMaskVector);
            LongVector<Shapes.S512Bit> z3 = predicateVector.add(x3).and(notMaskVector);
            LongVector<Shapes.S512Bit> z4 = predicateVector.add(x4).and(notMaskVector);

            z1.intoArray(results, 0);
            z2.intoArray(results, 8);
            z3.intoArray(results, 16);
            z4.intoArray(results, 24);

            result += Long.bitCount(results[0]) + Long.bitCount(results[1]) + Long.bitCount(results[2]) + Long.bitCount(results[3]);
            result += Long.bitCount(results[4]) + Long.bitCount(results[5]) + Long.bitCount(results[6]) + Long.bitCount(results[7]);
            result += Long.bitCount(results[8]) + Long.bitCount(results[9]) + Long.bitCount(results[10]) + Long.bitCount(results[11]);
            result += Long.bitCount(results[12]) + Long.bitCount(results[13]) + Long.bitCount(results[14]) + Long.bitCount(results[15]);
            result += Long.bitCount(results[16]) + Long.bitCount(results[17]) + Long.bitCount(results[18]) + Long.bitCount(results[19]);
            result += Long.bitCount(results[20]) + Long.bitCount(results[21]) + Long.bitCount(results[22]) + Long.bitCount(results[23]);
            result += Long.bitCount(results[24]) + Long.bitCount(results[25]) + Long.bitCount(results[26]) + Long.bitCount(results[27]);
            result += Long.bitCount(results[28]) + Long.bitCount(results[29]) + Long.bitCount(results[30]) + Long.bitCount(results[31]);
        }

//        System.out.println("result = " + result);
        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + Lamport2.class.getSimpleName() + ".*")
                .addProfiler(LinuxPerfProfiler.class)
                .warmupIterations(1)
                .measurementIterations(1)
                .threads(1)
                .detectJvmArgs()
                .build();

        new Runner(opt).run();
    }
}
