package pl.tkowalcz.sort;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class SortedArraysUnionTest {

    @Test
    public void shouldSortArray() {
        // Given
        int size = 5242880;

        int[] left = new Random(0)
                .ints(0, Integer.MAX_VALUE)
                .limit(size)
                .distinct()
                .toArray();

        int[] right = new Random(1)
                .ints(0, Integer.MAX_VALUE)
                .limit(size)
                .distinct()
                .toArray();

        Arrays.sort(left);
        Arrays.sort(right);

        int[] output = new int[size + size];
        int[] expected = IntStream.concat(Arrays.stream(left), Arrays.stream(right))
                .sorted()
                .distinct()
                .toArray();

        expected = Arrays.copyOf(expected, size + size);

        // When
        new SortedArraysUnion().swarUnion(left, right, output);

        // Then
        assertThat(output).isEqualTo(expected);
    }
}
