package pl.tkowalcz.varints;

import static org.testng.Assert.*;

public class CanonicalVarIntEncoderTest extends VarIntEncoderTest {

    @Override
    public VarIntEncoder createEncoder() {
        return CanonicalVarIntEncoder::encode;
    }
}
