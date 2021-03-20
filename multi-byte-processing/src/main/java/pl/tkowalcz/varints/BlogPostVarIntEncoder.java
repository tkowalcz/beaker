package pl.tkowalcz.varints;

/**
 * By Richard Startin: https://richardstartin.github.io/posts/dont-use-protobuf-for-telemetry
 */
public class BlogPostVarIntEncoder {

    private static final byte[] VAR_INT_LENGTHS = new byte[65];

    static {
        for (int i = 0; i <= 64; ++i) {
            VAR_INT_LENGTHS[i] = (byte) ((63 - i) / 7);
        }
    }

    static byte varIntLength(long value) {
        return VAR_INT_LENGTHS[Long.numberOfLeadingZeros(value)];
    }

    public static int encode(byte[] buffer, int offset, long value) {
        byte length = varIntLength(value);
        for (int i = 0; i < length; ++i) {
            buffer[offset + i] = ((byte) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        buffer[offset + length] = (byte) value;

        return offset + length + 1;
    }
}
