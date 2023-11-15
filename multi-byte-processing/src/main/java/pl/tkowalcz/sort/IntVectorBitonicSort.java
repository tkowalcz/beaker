package pl.tkowalcz.sort;

import jdk.incubator.vector.*;

import java.util.Arrays;

public class IntVectorBitonicSort {

    static final VectorSpecies<Integer> SPECIES = VectorSpecies.of(int.class, VectorShape.S_256_BIT);

    private static final VectorShuffle<Integer> SHUFFLE_0 = SPECIES.shuffleFromArray(new int[]{7, 6, 5, 4, 3, 2, 1, 0}, 0);

    private static final VectorShuffle<Integer> SHUFFLE_1 = SPECIES.shuffleFromArray(new int[]{4, 5, 6, 7, 0, 1, 2, 3}, 0);
    private static final VectorMask<Integer> MASK_1 = SPECIES.loadMask(new boolean[]{false, false, false, false, true, true, true, true}, 0);

    private static final VectorShuffle<Integer> SHUFFLE_2 = SPECIES.shuffleFromArray(new int[]{2, 3, 0, 1, 6, 7, 4, 5}, 0);
    private static final VectorMask<Integer> MASK_2 = SPECIES.loadMask(new boolean[]{false, false, true, true, false, false, true, true}, 0);

    private static final VectorShuffle<Integer> SHUFFLE_3 = SPECIES.shuffleFromArray(new int[]{1, 0, 3, 2, 5, 4, 7, 6}, 0);
    private static final VectorMask<Integer> MASK_3 = SPECIES.loadMask(new boolean[]{false, true, false, true, false, true, false, true}, 0);

    public static void sortElementsStartingFrom(int[] array, int leftIndex, int rightIndex) {
        int allThatRemains = rightIndex - leftIndex;
        if (allThatRemains <= 0) {
            return;
        }
//        if (allThatRemains <= SPECIES.length()) {
//            long mask = (-1L >> (64 - allThatRemains));
//            VectorMask<Integer> integerVectorMask = VectorMask.fromLong(SPECIES, mask);
//            IntVector input1 = IntVector.fromArray(SPECIES, array, leftIndex, );
//            SingleVectorBitonicSort.sort();
//        } else if ()
//            IntVector input1 = IntVector.fromArray(SPECIES, array, leftIndex);
//        IntVector input2 = IntVector.fromArray(SPECIES, array, rightIndex + SPECIES.length());
        Arrays.sort(array, leftIndex, rightIndex + 1);
    }

    public static void sortTwoVectorsStartingFrom(int[] array, int index) {
        IntVector input1 = IntVector.fromArray(SPECIES, array, index);
        IntVector input2 = IntVector.fromArray(SPECIES, array, index + SPECIES.length());

        input1 = SingleVectorBitonicSort.sort(input1);
        input2 = SingleVectorBitonicSort.sort(input2);

        {
            IntVector rearrangedVector = input1.rearrange(SHUFFLE_0);

            input1 = rearrangedVector.min(input2);
            input2 = rearrangedVector.max(input2);
        }
        {
            IntVector rearrangedVector = input1.rearrange(SHUFFLE_1);

            IntVector min = rearrangedVector.min(input1);
            IntVector max = rearrangedVector.max(input1);

            input1 = min.blend(max, MASK_1);
        }
        {
            IntVector rearrangedVector = input2.rearrange(SHUFFLE_1);

            IntVector min = rearrangedVector.min(input2);
            IntVector max = rearrangedVector.max(input2);

            input2 = min.blend(max, MASK_1);
        }
        {
            IntVector rearrangedVector = input1.rearrange(SHUFFLE_2);

            IntVector min = rearrangedVector.min(input1);
            IntVector max = rearrangedVector.max(input1);

            input1 = min.blend(max, MASK_2);
        }
        {
            IntVector rearrangedVector = input2.rearrange(SHUFFLE_2);

            IntVector min = rearrangedVector.min(input2);
            IntVector max = rearrangedVector.max(input2);

            input2 = min.blend(max, MASK_2);
        }
        {
            IntVector rearrangedVector = input1.rearrange(SHUFFLE_3);

            IntVector min = rearrangedVector.min(input1);
            IntVector max = rearrangedVector.max(input1);

            input1 = min.blend(max, MASK_3);
        }
        {
            IntVector rearrangedVector = input2.rearrange(SHUFFLE_3);

            IntVector min = rearrangedVector.min(input2);
            IntVector max = rearrangedVector.max(input2);

            input2 = min.blend(max, MASK_3);
        }

        input1.intoArray(array, index);
        input2.intoArray(array, index + SPECIES.length());
    }
}
