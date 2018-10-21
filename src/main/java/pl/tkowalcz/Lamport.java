package pl.tkowalcz;

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.Shapes;
import jdk.incubator.vector.Vector;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
public class Lamport {

    private static final int INPUT_SIZE = 1024 * 1024;

    private int[] values;
    private byte[] bValues;
    private byte[] bValuesCompressed;
    private short[] sValuesCompressed;
    private int[] iValuesCompressed;
    private long[] lValuesCompressed;

    @Setup
    public void setUp() {
        values = new Random(0).ints(0, 8)
                .limit(INPUT_SIZE)
                .toArray();

        bValues = new byte[INPUT_SIZE];
        for (int i = 0; i < bValues.length; i++) {
            bValues[i] = (byte) values[i];
        }

        bValuesCompressed = new byte[INPUT_SIZE / 2];
        for (int i = 0; i < values.length; i += 2) {
            bValuesCompressed[i / 2] = (byte) values[i];
            bValuesCompressed[i / 2] += (byte) (values[i + 1] << 4);
        }

        sValuesCompressed = new short[INPUT_SIZE / 4];
        for (int i = 0; i < values.length; i += 4) {
            sValuesCompressed[i / 4] = (byte) values[i];
            sValuesCompressed[i / 4] += (byte) (values[i + 1] << 4);
            sValuesCompressed[i / 4] += (byte) (values[i + 2] << 8);
            sValuesCompressed[i / 4] += (byte) (values[i + 3] << 12);
        }

        iValuesCompressed = new int[INPUT_SIZE / 8];
        for (int i = 0; i < values.length; i += 8) {
            iValuesCompressed[i / 8] = values[i];
            iValuesCompressed[i / 8] += (values[i + 1] << 4);
            iValuesCompressed[i / 8] += (values[i + 2] << 8);
            iValuesCompressed[i / 8] += (values[i + 3] << 12);

            iValuesCompressed[i / 8] += (values[i + 4] << 16);
            iValuesCompressed[i / 8] += (values[i + 5] << 20);
            iValuesCompressed[i / 8] += (values[i + 6] << 24);
            iValuesCompressed[i / 8] += (values[i + 7] << 28);
        }

        lValuesCompressed = new long[INPUT_SIZE / 16];
        for (int i = 0; i < values.length; i += 16) {
            lValuesCompressed[i / 16] = values[i];
            lValuesCompressed[i / 16] += (values[i + 1] << 4);
            lValuesCompressed[i / 16] += (values[i + 2] << 8);
            lValuesCompressed[i / 16] += (values[i + 3] << 12);

            lValuesCompressed[i / 16] += (values[i + 4] << 16);
            lValuesCompressed[i / 16] += (values[i + 5] << 20);
            lValuesCompressed[i / 16] += (values[i + 6] << 24);
            lValuesCompressed[i / 16] += (values[i + 7] << 28);

            lValuesCompressed[i / 16] += ((long) values[i + 8] << 32);
            lValuesCompressed[i / 16] += ((long) values[i + 9] << 36);
            lValuesCompressed[i / 16] += ((long) values[i + 10] << 40);
            lValuesCompressed[i / 16] += ((long) values[i + 11] << 44);

            lValuesCompressed[i / 16] += ((long) values[i + 12] << 48);
            lValuesCompressed[i / 16] += ((long) values[i + 13] << 52);
            lValuesCompressed[i / 16] += ((long) values[i + 14] << 56);
            lValuesCompressed[i / 16] += ((long) values[i + 15] << 60);
        }
    }

    @Benchmark
    public int plainIf() {
        int result = 0;

        for (int i = 0; i < values.length; i++) {
            if (values[i] < 5) {
                result++;
            }
        }

        return result;
    }

    @Benchmark
    public int plainArithmetic() {
        int result = 0;

        int y = 5;
        int mask = 0b0000_0111;

        for (int i = 0; i < values.length; i++) {
            int x = values[i] ^ mask;
            int z = (y + x) & ~mask;

//            result += z >>> 3;
            result += Integer.bitCount(z);
        }

        return result;
    }

    @Benchmark
    public int plainArithmetic_x1() {
        int result = 0;

        int y = 5;
        byte mask = 0b0000_0111;

        for (int i = 0; i < bValues.length; i++) {
            int x = bValues[i] ^ mask;
            int z = (y + x) & ~mask;

//            result += z >>> 3;
            result += Integer.bitCount(z);
        }

        return result;
    }

    @Benchmark
    public int plainArithmetic_x2() {
        int result = 0;

        int y = (5 << 4) + 5;
        int mask = 0b0111_0111;

        for (int i = 0; i < bValuesCompressed.length; i++) {
            int x = bValuesCompressed[i] ^ mask;
            int z = (y + x) & ~mask;

//            result += ((z & 0x1111) >>> 3) + (z >>> 7);
            result += Integer.bitCount(z);
        }

        return result;
    }

    @Benchmark
    public int plainArithmetic_x4() {
        int result = 0;

        int y = (5 << 12) + (5 << 8) + (5 << 4) + 5;
        int mask = 0b0111_0111_0111_0111;

        for (int i = 0; i < sValuesCompressed.length; i++) {
            int x = sValuesCompressed[i] ^ mask;
            int z = (y + x) & ~mask;

//            result += ((z >>> 3) & 0x1) + ((z >>> 7) & 0x1) + ((z >>> 11) & 0x1) + (z >>> 15);
            result += Integer.bitCount(z);
        }

        return result;
    }

    @Benchmark
    public int plainArithmetic_x8() {
        int result = 0;

        int y = (5 << 28) + (5 << 24) + (5 << 20) + (5 << 16) + (5 << 12) + (5 << 8) + (5 << 4) + 5;
        int mask = 0b0111_0111_0111_0111_0111_0111_0111_0111;

        for (int i = 0; i < iValuesCompressed.length; i++) {
            int x = iValuesCompressed[i] ^ mask;
            int z = (y + x) & ~mask;

//            result += ((z >>> 3) & 0x1) + ((z >>> 7) & 0x1) + ((z >>> 11) & 0x1) + (z >>> 15);
            result += Integer.bitCount(z);
        }

        return result;
    }

    @Benchmark
    public int plainArithmetic_x16() {
        int result = 0;

        long y = (5L << 60) + (5L << 56) + (5L << 52) + (5L << 48) + (5L << 44) + (5L << 40) + (5L << 36) + (5L << 32) + (5L << 28) + (5L << 24) + (5L << 20) + (5L << 16) + (5L << 12) + (5L << 8) + (5L << 4) + 5L;
        long mask = 0b0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111L;

        for (int i = 0; i < lValuesCompressed.length; i++) {
            long x = lValuesCompressed[i] ^ mask;
            long z = (y + x) & ~mask;

//            result += ((z >>> 3) & 0x1) + ((z >>> 7) & 0x1) + ((z >>> 11) & 0x1) + (z >>> 15);
            result += Long.bitCount(z);
        }

        return result;
    }

    private static final LongVector.LongSpecies<Shapes.S256Bit> species =
            (LongVector.LongSpecies<Shapes.S256Bit>) Vector.species(long.class, Shapes.S_256_BIT);

    @Benchmark
    public int plainArithmetic_x32() {
        int result = 0;

        long _y1 = (5L << 60) + (5L << 56) + (5L << 52) + (5L << 48) + (5L << 44) + (5L << 40) + (5L << 36) + (5L << 32) + (5L << 28) + (5L << 24) + (5L << 20) + (5L << 16) + (5L << 12) + (5L << 8) + (5L << 4) + 5L;
        long[] _y2 = new long[]{_y1, _y1, _y1, _y1};

        long _mask1 = 0b0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111_0111L;
        long[] _mask2 = new long[]{_mask1, _mask1, _mask1, _mask1};

        LongVector<Shapes.S256Bit> y = species.fromArray(_y2, 0);
        LongVector<Shapes.S256Bit> mask = species.fromArray(_mask2, 0);

        for (int i = 0; i < lValuesCompressed.length; i += species.elementSize()) {
            LongVector<Shapes.S256Bit> v = species.fromArray(lValuesCompressed, i);
            LongVector<Shapes.S256Bit> x = v.xor(mask);
            LongVector<Shapes.S256Bit> z = y.add(x).and(mask.not());

            z.shiftR(3);
//            result += ((z >>> 3) & 0x1) + ((z >>> 7) & 0x1) + ((z >>> 11) & 0x1) + (z >>> 15);
//            result += z.;
        }

        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + Lamport.class.getSimpleName() + ".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .detectJvmArgs()
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
