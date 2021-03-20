package pl.tkowalcz.varints;

public class NoLoop32bitVarIntEncoder {

    private static final byte[] VAR_INT_LENGTHS = new byte[33];

    static {
        for (int i = 0; i <= 32; ++i) {
            VAR_INT_LENGTHS[i] = (byte) ((31 - i) / 7);
        }
    }

    public static int encode(byte[] buffer, int offset, int value) {
        int length = varIntLength(value);

        buffer[offset] = (byte) (value | 0x80);
        buffer[offset + 1] = (byte) (value >>> 7 | 0x80);
        buffer[offset + 2] = (byte) (value >>> 14 | 0x80);
        buffer[offset + 3] = (byte) (value >>> 21 | 0x80);
        buffer[offset + 4] = (byte) (value >>> 28);

        buffer[offset + length] = (byte) (buffer[offset + length] & 0x7F);
        return offset + length + 1;
    }

    static byte varIntLength(int value) {
        return VAR_INT_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }
}
