package pl.tkowalcz.varints;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorOperators;

public class Vectorized128VarIntEncoder {

    private static final ByteVector shifts1 = ByteVector.fromArray(ByteVector.SPECIES_128, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 0, 0, 0, 0, 0, 0, 0}, 0);
    private static final ByteVector shifts2 = ByteVector.fromArray(ByteVector.SPECIES_128, new byte[]{0, 7, 6, 5, 4, 3, 2, 1, 0, 7, 0, 0, 0, 0, 0, 0}, 0);

    private static final ByteVector mask2 = ByteVector.broadcast(ByteVector.SPECIES_128, (byte) 0x80);

    private static final long[] TEMP_ARRAY = new long[2];

    public static int encode(byte[] buffer, int offset, long[] values, int index) {
        long value = values[index];

        TEMP_ARRAY[0] = value << 8;
        TEMP_ARRAY[1] = (value >>> 56) | (value >>> 56) << 8;

        ByteVector v1 = LongVector
                .fromArray(LongVector.SPECIES_128, values, index)
                .reinterpretAsBytes();

        ByteVector v2 = LongVector
                .fromArray(LongVector.SPECIES_128, TEMP_ARRAY, 0)
                .reinterpretAsBytes();

        v2
                .lanewise(VectorOperators.LSHR, shifts2)
                .add(v1.lanewise(VectorOperators.LSHL, shifts1))
                .or(mask2)
                .intoArray(buffer, offset);

        byte length = BlogPostVarIntEncoder.varIntLength(values[index]);
        buffer[offset + length] = (byte) (buffer[offset + length] & 0x7F);

        return offset + length + 1;
    }
}
