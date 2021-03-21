package pl.tkowalcz.varints;

import static org.testng.Assert.*;

public class VectorizedVarIntEncoderTest extends VarIntEncoderTest {

    @Override
    public VarIntEncoder createEncoder() {
        return (buffer, offset, value) -> VectorizedVarIntEncoder.encode(
                buffer,
                offset,
                new long[]{value, 0},
                0
        );
    }
}
