package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorShuffle;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.assertj.core.api.Assertions.assertThat;

public class DoubleVectorBitonicSortTest {

    @Test
    public void shouldSort() {
        // Given
        int[] inputArray1 = {42, 33, 56, 76, 3, 89, 124, 22};
        int[] inputArray2 = {1, 18, 32, 6, 21, 13, 3, 80};
        int[] expected = {1, 3, 3, 6, 13, 18, 21, 22, 32, 33, 42, 56, 76, 80, 89, 124};

        IntVector input1 = IntVector.fromArray(SingleVectorBitonicSort.SPECIES_I256, inputArray1, 0);
        IntVector input2 = IntVector.fromArray(SingleVectorBitonicSort.SPECIES_I256, inputArray2, 0);

        // When
        IntVector[] actual = DoubleVectorBitonicSort.sort(input1, input2);

        // Then
        assertThat(actual).hasSize(2);
        assertThat(
                addAll(
                        actual[0].toArray(),
                        actual[1].toArray()
                )
        ).isEqualTo(expected);
    }
}
