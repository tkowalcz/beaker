package pl.tkowalcz.varints;

@FunctionalInterface
public interface VarIntEncoder {

    int encode(byte[] buffer, int offset, long value);
}
