package pl.tkowalcz.sort;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorMask;

public class VectorizedHistogram {

    private static final long[] LUT;

    static {
        LUT = new long[64];
        for (int i = 0; i < LUT.length; i++) {
            LUT[i] = 1L << i;
        }
    }

    private final byte[] level0 = new byte[256];
    private final byte[] level1 = new byte[256];
    private final byte[] level2 = new byte[256];
    private final byte[] level3 = new byte[256];

    public void calculate(int[] input) {
        ByteVector level0 = ByteVector.zero(ByteVector.SPECIES_512);
        ByteVector level1 = ByteVector.zero(ByteVector.SPECIES_512);
        ByteVector level2 = ByteVector.zero(ByteVector.SPECIES_512);
        ByteVector level3 = ByteVector.zero(ByteVector.SPECIES_512);

        for (int value : input) {
            value -= Integer.MIN_VALUE;

            int forLevel0 = value & 0x3F;
            level0 = level0.add((byte) 1, VectorMask.fromLong(ByteVector.SPECIES_512, LUT[forLevel0]));

            int forLevel1 = value >>> 8 & 0x3F;
            level1 = level1.add((byte) 1, VectorMask.fromLong(ByteVector.SPECIES_512, LUT[forLevel1]));

            int forLevel2 = value >>> 16 & 0x3F;
            level2 = level2.add((byte) 1, VectorMask.fromLong(ByteVector.SPECIES_512, LUT[forLevel2]));

            int forLevel3 = value >>> 24 & 0x3F;
            level3 = level3.add((byte) 1, VectorMask.fromLong(ByteVector.SPECIES_512, LUT[forLevel3]));
        }

        level0.intoArray(this.level0, 0);
        level1.intoArray(this.level1, 0);
        level2.intoArray(this.level2, 0);
        level3.intoArray(this.level3, 0);
    }

    public byte[] getLevel0() {
        return level0;
    }

    public byte[] getLevel1() {
        return level1;
    }

    public byte[] getLevel2() {
        return level2;
    }

    public byte[] getLevel3() {
        return level3;
    }
}
