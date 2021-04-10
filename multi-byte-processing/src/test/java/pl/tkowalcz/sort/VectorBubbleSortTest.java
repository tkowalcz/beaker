package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VectorBubbleSortTest {

    @Test
    public void shouldSort() {
        // Given
        int[] inputArray = {42, 33, 56, 76, 3, 89, 124, 22};
        int[] expected = {3, 22, 33, 42, 56, 76, 89, 124};

        VectorBubbleSort bubbleSort = new VectorBubbleSort();
        IntVector input = IntVector.fromArray(VectorBubbleSort.SPECIES_I256, inputArray, 0);

        // When
        input = bubbleSort.sort(input);

        // Then
        assertThat(input.toArray()).isEqualTo(expected);
    }
}
