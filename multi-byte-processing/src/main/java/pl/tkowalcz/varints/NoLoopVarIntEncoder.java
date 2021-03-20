package pl.tkowalcz.varints;

public class NoLoopVarIntEncoder {

    public static int encode(byte[] buffer, int offset, long value) {
        buffer[offset] = (byte) (value | 0x80);
        buffer[offset + 1] = (byte) (value >>> 7 | 0x80);
        buffer[offset + 2] = (byte) (value >>> 14 | 0x80);
        buffer[offset + 3] = (byte) (value >>> 21 | 0x80);
        buffer[offset + 4] = (byte) (value >>> 28 | 0x80);
        buffer[offset + 5] = (byte) (value >>> 35 | 0x80);
        buffer[offset + 6] = (byte) (value >>> 42 | 0x80);
        buffer[offset + 7] = (byte) (value >>> 49 | 0x80);
        buffer[offset + 8] = (byte) (value >>> 56 | 0x80);
        buffer[offset + 9] = (byte) (value >>> 63);

        byte length = BlogPostVarIntEncoder.varIntLength(value);
        buffer[offset + length] = (byte) (buffer[offset + length] & 0x7F);

        return offset + length + 1;
    }
}
