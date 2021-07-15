package pl.tkowalcz.sort;

import jdk.incubator.vector.*;

public class SingleVectorBitonicSort {

    static final VectorSpecies<Integer> SPECIES_I256 = VectorSpecies.of(int.class, VectorShape.S_256_BIT);

    private static final VectorShuffle<Integer> SHUFFLE_1 = SPECIES_I256.shuffleFromArray(new int[]{1, 0, 3, 2, 5, 4, 7, 6}, 0);
    private static final VectorMask<Integer> MASK_1 = SPECIES_I256.loadMask(new boolean[]{false, true, false, true, false, true, false, true}, 0); // 0xAA

    private static final VectorShuffle<Integer> SHUFFLE_2 = SPECIES_I256.shuffleFromArray(new int[]{3, 2, 1, 0, 7, 6, 5, 4}, 0);
    private static final VectorMask<Integer> MASK_2 = SPECIES_I256.loadMask(new boolean[]{false, false, true, true, false, false, true, true}, 0); // 0xCC

    private static final VectorShuffle<Integer> SHUFFLE_3 = SHUFFLE_1;
    private static final VectorMask<Integer> MASK_3 = MASK_1;

    private static final VectorShuffle<Integer> SHUFFLE_4 = SPECIES_I256.shuffleFromArray(new int[]{7, 6, 5, 4, 3, 2, 1, 0}, 0);
    private static final VectorMask<Integer> MASK_4 = SPECIES_I256.loadMask(new boolean[]{false, false, false, false, true, true, true, true}, 0); // 0xF0

    private static final VectorShuffle<Integer> SHUFFLE_5 = SPECIES_I256.shuffleFromArray(new int[]{2, 3, 0, 1, 6, 7, 4, 5}, 0);
    private static final VectorMask<Integer> MASK_5 = MASK_2;

    private static final VectorShuffle<Integer> SHUFFLE_6 = SHUFFLE_1;
    private static final VectorMask<Integer> MASK_6 = MASK_1;

    public static IntVector sort(IntVector input) {
        {
            IntVector rearrangedVector = input.rearrange(SHUFFLE_1);

            IntVector min = rearrangedVector.min(input);
            IntVector max = rearrangedVector.max(input);

            input = min.blend(max, MASK_1);
        }
        {
            IntVector rearrangedVector = input.rearrange(SHUFFLE_2);

            IntVector min = rearrangedVector.min(input);
            IntVector max = rearrangedVector.max(input);

            input = min.blend(max, MASK_2);
        }
        {
            IntVector rearrangedVector = input.rearrange(SHUFFLE_3);

            IntVector min = rearrangedVector.min(input);
            IntVector max = rearrangedVector.max(input);

            input = min.blend(max, MASK_3);
        }
        {
            IntVector rearrangedVector = input.rearrange(SHUFFLE_4);

            IntVector min = rearrangedVector.min(input);
            IntVector max = rearrangedVector.max(input);

            input = min.blend(max, MASK_4);
        }
        {
            IntVector rearrangedVector = input.rearrange(SHUFFLE_5);

            IntVector min = rearrangedVector.min(input);
            IntVector max = rearrangedVector.max(input);

            input = min.blend(max, MASK_5);
        }
        {
            IntVector rearrangedVector = input.rearrange(SHUFFLE_6);

            IntVector min = rearrangedVector.min(input);
            IntVector max = rearrangedVector.max(input);

            input = min.blend(max, MASK_6);
        }

        return input;
    }
}
