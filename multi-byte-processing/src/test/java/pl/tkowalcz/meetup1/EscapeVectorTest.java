package pl.tkowalcz.meetup1;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EscapeVectorTest {

    @Test
    void shouldEscapeCharactersSequential() {
        // Given
        String input = "C:\\Program Files\\Internet Explorer\\ie6.exe";
        String expected = "C:\\\\Program Files\\\\Internet Explorer\\\\ie6.exe";
        byte[] actual = new byte[1024];

        // When
        EscapeVector.escape(input.getBytes(StandardCharsets.US_ASCII), actual);

        // Then
        assertThat(new String(actual).trim()).isEqualTo(expected);
    }

    @Test
    void testExpand() {
        // Given
        IntVector vector = IntVector.fromArray(IntVector.SPECIES_PREFERRED, new int[]{1, 2, 3, 4}, 0);
        VectorMask<Integer> mask = VectorMask.fromValues(IntVector.SPECIES_PREFERRED, false, true, false, true);

        // When
        IntVector actual = vector.expand(mask);

        // Then
        assertThat(actual.toArray()).containsExactly(0, 1, 0, 2);
    }

    @Test
    void shouldEscapeCharactersVector() {
        // Given
        String input = "C:\\Program Files\\Internet Explorer\\ie6.exe                   ";
        String expected = "C:\\\\Program Files\\\\Internet Explorer\\\\ie6.exe";
        byte[] actual = new byte[1024];

        // When
        EscapeVector.vectorEscape(input.getBytes(StandardCharsets.US_ASCII), actual);

        // Then
        assertThat(new String(actual).trim()).isEqualTo(expected);
    }
}
