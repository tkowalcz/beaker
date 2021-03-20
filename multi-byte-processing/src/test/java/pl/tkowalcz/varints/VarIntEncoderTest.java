package pl.tkowalcz.varints;

import com.google.protobuf.CodedOutputStream;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public abstract class VarIntEncoderTest {

    public abstract VarIntEncoder createEncoder();

    private final VarIntEncoder encoder = createEncoder();

    @Test
    public void shouldEncodeOneByteValue() {
        // Given
        long value = 0x23;
        byte[] buffer = new byte[10];

        // When
        int offset = encoder.encode(buffer, 0, value);

        // Then
        buffer = Arrays.copyOfRange(buffer, 0, offset);
        assertThat(buffer).containsExactly(0x23);
    }

    @Test
    public void shouldEncode8bitValue() {
        // Given
        long value = 0xFF;
        byte[] buffer = new byte[10];

        // When
        int offset = encoder.encode(buffer, 0, value);

        // Then
        buffer = Arrays.copyOfRange(buffer, 0, offset);
        assertThat(buffer).containsExactly(0xFF, 0x01);
    }

    @Test
    public void shouldEncode16bitValue() {
        // Given
        long value = 0xAE_FF;
        byte[] buffer = new byte[10];

        // When
        int offset = encoder.encode(buffer, 0, value);

        // Then
        buffer = Arrays.copyOfRange(buffer, 0, offset);
        assertThat(buffer).containsExactly(0xFF, 0xDD, 0x02);
    }

    @Test
    public void shouldMatchReferenceImplementationWhenEncodingRandomSequence() throws IOException {
        // Given
        long[] input = getRandomInputs();

        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        CodedOutputStream referenceImplementation = CodedOutputStream.newInstance(expected);

        byte[] buffer = new byte[1000_000 * 10];

        // When
        int offset = 0;
        for (long value : input) {
            offset = encoder.encode(buffer, offset, value);
            referenceImplementation.writeInt64NoTag(value);
        }

        // Then
        referenceImplementation.flush();
        buffer = Arrays.copyOfRange(buffer, 0, offset);
        byte[] values = expected.toByteArray();
        assertThat(buffer).containsExactly(values);
    }

    long[] getRandomInputs() {
        return ThreadLocalRandom
                .current()
                .longs(1000_000, 0, Long.MAX_VALUE)
                .toArray();
    }
}
