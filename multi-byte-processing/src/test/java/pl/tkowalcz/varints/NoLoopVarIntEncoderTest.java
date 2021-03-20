package pl.tkowalcz.varints;

import static org.testng.Assert.*;

public class NoLoopVarIntEncoderTest extends VarIntEncoderTest {

    @Override
    public VarIntEncoder createEncoder() {
        return NoLoopVarIntEncoder::encode;
    }
}
