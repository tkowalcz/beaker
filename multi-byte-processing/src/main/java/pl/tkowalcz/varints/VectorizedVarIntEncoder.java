package pl.tkowalcz.varints;

import jdk.incubator.vector.*;

public class VectorizedVarIntEncoder {

    private static final ByteVector shifts1 = ByteVector.fromArray(ByteVector.SPECIES_128, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 0, 0, 0, 0, 0, 0, 0}, 0);
    private static final ByteVector shifts2 = ByteVector.fromArray(ByteVector.SPECIES_128, new byte[]{0, 7, 6, 5, 4, 3, 2, 1, 0, 7, 0, 0, 0, 0, 0, 0}, 0);

    private static final ByteVector mask2 = ByteVector.broadcast(ByteVector.SPECIES_128, (byte) 0x80);

    public static int encode(byte[] buffer, int offset, long[] values, int index) {
        ByteVector v1 = LongVector.fromArray(LongVector.SPECIES_128, values, index)
                .reinterpretAsBytes();

        v1 = v1.lanewise(VectorOperators.LSHL, shifts1);

        long value = values[index];
        ByteVector v2 = LongVector.fromArray(LongVector.SPECIES_128, new long[]{value << 8, (value >>> 56) | (value >>> 56) << 8}, index)
                .reinterpretAsBytes();

        v2 = v2.lanewise(VectorOperators.LSHR, shifts2);
        v2 = v2.add(v1).or(mask2);
        v2.intoArray(buffer, offset);

        byte length = BlogPostVarIntEncoder.varIntLength(values[index]);
        buffer[offset + length] = (byte) (buffer[offset + length] & 0x7F);

        return offset + length + 1;
    }
}
