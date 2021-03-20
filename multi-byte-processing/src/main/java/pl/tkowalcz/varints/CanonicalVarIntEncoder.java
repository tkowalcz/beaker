package pl.tkowalcz.varints;

public class CanonicalVarIntEncoder {

    public static int encode(byte[] buffer, int offset, long value) {
        while (true) {
            if ((value & ~0x07FL) == 0) {
                buffer[offset++] = (byte) value;
                break;
            } else {
                buffer[offset++] = (byte) ((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }

        return offset;
    }
}
