package pl.tkowalcz.varints;

public class Vectorized128VarIntEncoderTest extends VarIntEncoderTest {

    @Override
    public VarIntEncoder createEncoder() {
        return (buffer, offset, value) -> Vectorized128VarIntEncoder.encode(
                buffer,
                offset,
                new long[]{value, 0},
                0
        );
    }
}
