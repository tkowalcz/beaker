package pl.tkowalcz.sort;

import jdk.incubator.vector.*;

public class VectorizedQuickSort {

    public static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_128;

    /**
     * @noinspection unchecked
     */
    private static final VectorShuffle<Integer>[] SHUFFLE_LUT = new VectorShuffle[]{
            VectorShuffle.fromValues(SPECIES, 0, 1, 2, 3), // 0b0000
            VectorShuffle.fromValues(SPECIES, 0, 1, 2, 3), // 0b0001
            VectorShuffle.fromValues(SPECIES, 1, 1, 2, 3), // 0b0010
            VectorShuffle.fromValues(SPECIES, 0, 1, 2, 3), // 0b0011

            VectorShuffle.fromValues(SPECIES, 2, 1, 2, 3), // 0b0100
            VectorShuffle.fromValues(SPECIES, 0, 2, 2, 3), // 0b0101
            VectorShuffle.fromValues(SPECIES, 1, 2, 2, 3), // 0b0110
            VectorShuffle.fromValues(SPECIES, 0, 1, 2, 3), // 0b0111

            VectorShuffle.fromValues(SPECIES, 3, 1, 2, 3), // 0b1000
            VectorShuffle.fromValues(SPECIES, 0, 3, 2, 3), // 0b1001
            VectorShuffle.fromValues(SPECIES, 1, 3, 2, 3), // 0b1010
            VectorShuffle.fromValues(SPECIES, 0, 1, 3, 3), // 0b1011

            VectorShuffle.fromValues(SPECIES, 2, 3, 2, 3), // 0b1100
            VectorShuffle.fromValues(SPECIES, 0, 2, 3, 3), // 0b1101
            VectorShuffle.fromValues(SPECIES, 1, 2, 3, 3), // 0b1110
            VectorShuffle.fromValues(SPECIES, 0, 1, 2, 3), // 0b1111
    };

    public static void sort(
            int[] input,
            int[] output,
            int left,
            int right,
            int pivot
    ) {
        IntVector pivotVec = IntVector.broadcast(SPECIES, pivot);

        int position = left;
        while (position < right) {
            IntVector leftVec = IntVector.fromArray(SPECIES, input, position);

            VectorMask<Integer> ltMask = leftVec.compare(VectorOperators.LT, pivotVec);
            leftVec.rearrange(SHUFFLE_LUT[(int) ltMask.toLong()]);

            leftVec.intoArray(output, position, ltMask);
            leftVec.intoArray(input, position, ltMask.not());

            position += SPECIES.length();
        }
    }
}
