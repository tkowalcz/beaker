package pl.tkowalcz.meetup1;

import jdk.incubator.vector.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.testng.annotations.DataProvider;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
    void testReinterpretShape() {
        // Given
        IntVector vector = IntVector.fromArray(IntVector.SPECIES_256, new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 0);

        // WhenR
        LongVector actual = (LongVector) vector.convertShape(VectorOperators.I2L, LongVector.SPECIES_512, 0);

        // Then
        assertThat(actual.toArray()).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
    }

    @Test
    void testLeftShift() {
        // Given
        IntVector vector = IntVector.fromArray(IntVector.SPECIES_256, new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 0);

        // When
        VectorShuffle<Integer> shuffle = VectorShuffle.fromArray(IntVector.SPECIES_256, new int[]{1, 2, 3, 4, 5, 6, 7, 0}, 0);
        IntVector actual = vector.rearrange(shuffle);

        // Then
        assertThat(actual.toArray()).containsExactly(2, 3, 4, 5, 6, 7, 8, 0);
    }

    @Test
    void testLeftShift_Slice() {
        // Given
        IntVector vector = IntVector.fromArray(IntVector.SPECIES_256, new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 0);

        // When
        IntVector actual = vector.slice(1);

        // Then
        assertThat(actual.toArray()).containsExactly(2, 3, 4, 5, 6, 7, 8, 0);
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
