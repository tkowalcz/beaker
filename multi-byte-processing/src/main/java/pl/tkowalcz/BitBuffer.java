package pl.tkowalcz;

import java.nio.ByteBuffer;

public class BitBuffer {

    private long position;
    private final ByteBuffer buffer;

    public BitBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void put(long newValue, int significantBits) {
        int bitWidthMask = (1 << significantBits) - 1;
        newValue = newValue & bitWidthMask;

        int index = ((int) (position / Long.SIZE)) * Long.BYTES;
        long value = buffer.getLong(index);

        int bitsAvailable = Long.SIZE - (int) (position % Long.SIZE);
        if (bitsAvailable >= significantBits) {
            int shiftAMount = bitsAvailable - significantBits;

            value |= newValue << shiftAMount;
            buffer.putLong(index, value);
            position += significantBits;
        } else {
            int bitsMissing = significantBits - bitsAvailable;

            value |= newValue >>> bitsMissing;
            buffer.putLong(index, value);
            position += bitsAvailable;

            put(newValue, bitsMissing);
        }
    }
}
