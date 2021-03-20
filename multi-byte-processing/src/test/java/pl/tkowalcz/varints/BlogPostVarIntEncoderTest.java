package pl.tkowalcz.varints;

import static org.testng.Assert.*;

public class BlogPostVarIntEncoderTest extends VarIntEncoderTest {

    @Override
    public VarIntEncoder createEncoder() {
        return BlogPostVarIntEncoder::encode;
    }
}
