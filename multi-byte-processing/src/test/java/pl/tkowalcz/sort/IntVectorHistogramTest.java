package pl.tkowalcz.sort;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntVectorHistogramTest {

    @Test
    void shouldCalculateMinAndMax() {
        // Given
        int[] array = {1, 2, 3, 4, 5, 6, 7, 90, 80, 70, 60, 50, 40, 30, 20};

        // When
        Pair<Integer, Integer> result = IntVectorHistogram.adaptiveHistogram(array);

        // Then
        Assertions.assertThat(result).isEqualByComparingTo(Pair.of(1, 90));
    }
}