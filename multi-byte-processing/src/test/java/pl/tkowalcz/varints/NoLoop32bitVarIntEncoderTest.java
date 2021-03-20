package pl.tkowalcz.varints;

import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.*;

public class NoLoop32bitVarIntEncoderTest extends VarIntEncoderTest {

    @Override
    public VarIntEncoder createEncoder() {
        return (buffer, offset, value) -> NoLoop32bitVarIntEncoder.encode(buffer, offset, (int) value);
    }

    @Override
    long[] getRandomInputs() {
        return ThreadLocalRandom
                .current()
                .ints(1000_000, 0, Integer.MAX_VALUE)
                .asLongStream()
                .toArray();
    }
}
