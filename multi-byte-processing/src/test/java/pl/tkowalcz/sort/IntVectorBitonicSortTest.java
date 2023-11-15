package pl.tkowalcz.sort;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IntVectorBitonicSortTest {

    @Test
    void shouldSort1() {
        // Given
        int[] array = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};

        // When
        IntVectorBitonicSort.sortTwoVectorsStartingFrom(array, 0);

        // Then
        assertThat(array).isSorted();
    }

    @Test
    void shouldSort2() {
        // Given
        int[] array = {2343, 423, 3211, 1212, 121, 121, 124, 5345, 4, 42, 97, 555, 778, 55, 43, 8};

        // When
        IntVectorBitonicSort.sortTwoVectorsStartingFrom(array, 0);

        // Then
        assertThat(array).isSorted();
    }
}