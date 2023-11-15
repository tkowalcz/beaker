package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class VectorizedQuickSortTest {

    public static final int ARRAY_SIZE = 64;

    private int[] array;

    @BeforeEach
    public void setUp() {
        array = new Random().ints(ARRAY_SIZE, 0, 1024).toArray();
    }

    @Test
    void shouldPartitionVectors1() {
        // Given
        array = new int[]{42, 32, 65, 77, 82, 342, 54, 19, 84, 903, 32, 11, 0, 3, 2, 60};

        IntVector pivotVector = IntVector.broadcast(VectorizedQuickSort.SPECIES, 60);
        IntVector intVector = IntVector.fromArray(VectorizedQuickSort.SPECIES, array, 0);

        // When
        Pair<Integer, Integer> result = VectorizedQuickSort.partitionVectors(array, intVector, pivotVector, 0, 16);

        // Then
        assertThat(array).containsExactly(
                42, 32, 54, 19,
                82, 342, 54, 19, 84, 903, 32, 11,
                65, 77, 82, 342);
    }

    @Test
    void shouldPartitionVectors2() {
        // Given
        array = new int[]{42, 32, 65, 77, 82, 342, 54, 19, 84, 903, 32, 11, 0, 3, 2, 600};

        IntVector pivotVector = IntVector.broadcast(VectorizedQuickSort.SPECIES, 600);
        IntVector intVector = IntVector.fromArray(VectorizedQuickSort.SPECIES, array, 0);

        // When
        VectorizedQuickSort.partitionVectors(array, intVector, pivotVector, 0, 16);

        // Then
        assertThat(array).containsExactly(
                42, 32, 65, 77, 82, 342, 54, 19,
                84, 903, 32, 11, 0, 3, 2, 600
        );
    }

    @Test
    void shouldPartitionVectors3() {
        // Given
        array = new int[]{42, 32, 65, 77, 82, 342, 54, 19, 84, 903, 32, 11, 0, 3, 2, 600};

        IntVector pivotVector = IntVector.broadcast(VectorizedQuickSort.SPECIES, -1);
        IntVector intVector = IntVector.fromArray(VectorizedQuickSort.SPECIES, array, 0);

        // When
        VectorizedQuickSort.partitionVectors(array, intVector, pivotVector, 0, 16);

        // Then
        assertThat(array).containsExactly(
                42, 32, 65, 77, 82, 342, 54, 19,
                42, 32, 65, 77, 82, 342, 54, 19
        );
    }

    @Test
    void shouldPartitionRemainingVectors1() {
        // Given
        array = new int[]{42, 32, 65, 77, 82, 342, 54, 19, 84, 903, 32, 11, 0, 3, 2, 60};

        IntVector pivotVector = IntVector.broadcast(VectorizedQuickSort.SPECIES, 60);

        // When
        Pair<Integer, Integer> result = VectorizedQuickSort.partitionRemaining(array, pivotVector, 5, 9, 0, 6);

        // Then
//        assertThat(result)
        assertThat(array).containsExactly(
                54, 19, 342, 84,
                342, 54, 19, 84, 903, 32, 11, 0, 3, 2, 60);
    }

    @Test
    public void shouldSortRandomArray() {
        // Given
        int[] expected = array.clone();
        Arrays.sort(expected);

        // When
        VectorizedQuickSort.sort(array);

        // Then
        assertThat(array).isSorted();
        assertThat(array).containsExactly(expected);
    }
}
