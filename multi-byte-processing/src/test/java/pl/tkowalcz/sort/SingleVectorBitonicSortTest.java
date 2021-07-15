package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorShuffle;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SingleVectorBitonicSortTest {

    @Test
    public void shouldSort() {
        // Given
        int[] inputArray = {42, 33, 56, 76, 3, 89, 124, 22};
        int[] expected = {3, 22, 33, 42, 56, 76, 89, 124};

        IntVector input = IntVector.fromArray(SingleVectorBitonicSort.SPECIES_I256, inputArray, 0);

        // When
        input = SingleVectorBitonicSort.sort(input);

        // Then
        assertThat(input.toArray()).isEqualTo(expected);
    }

    @Test
    public void shouldName() {
        // Given
        int[] inputArray = {42, 33, 56, 76, 3, 89, 124, 22};
        IntVector input = IntVector.fromArray(SingleVectorBitonicSort.SPECIES_I256, inputArray, 0);
        VectorShuffle<Integer> shuffle = SingleVectorBitonicSort.SPECIES_I256.shuffleFromArray(new int[]{1, 0, 3, 2, 5, 4, 7, 6}, 0);

        IntVector expected = input.rearrange(shuffle);

        // When
        IntVector actual = input.rearrange(shuffle, SingleVectorBitonicSort.SPECIES_I256.maskAll(true));

        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
