package pl.tkowalcz.sort;

import jdk.incubator.vector.*;

public class VectorBubbleSort {

    static final VectorSpecies<Integer> SPECIES_I256 = VectorSpecies.of(int.class, VectorShape.S_256_BIT);

    private static final VectorShuffle<Integer> SHUFFLE_ODD = SPECIES_I256.shuffleFromArray(new int[]{1, 0, 3, 2, 5, 4, 7, 6}, 0);
    private static final VectorShuffle<Integer> SHUFFLE_EVEN = SPECIES_I256.shuffleFromArray(new int[]{0, 2, 1, 4, 3, 6, 5, 7}, 0);

    private boolean[] maskArray = new boolean[SPECIES_I256.length()];

    public IntVector sort(IntVector input) {
        for (int i = 0; i < 4; i++) {
            {
                IntVector rearrangedVector = input.rearrange(SHUFFLE_ODD);

                VectorMask<Integer> lessThan = rearrangedVector.compare(VectorOperators.LT, input);
                VectorMask<Integer> integerMask = getMaskOdd(lessThan);
                input = input.blend(rearrangedVector, integerMask);
            }
            {
                IntVector rearrangedVector = input.rearrange(SHUFFLE_EVEN);

                VectorMask<Integer> lessThan = rearrangedVector.compare(VectorOperators.LT, input);
                VectorMask<Integer> integerMask = getMaskEven(lessThan);
                input = input.blend(rearrangedVector, integerMask);
            }
        }

        return input;
    }

    private VectorMask<Integer> getMaskOdd(VectorMask<Integer> mask) {
        long maskLong = mask.toLong();
        maskLong = (maskLong & 0x55) | ((maskLong & 0x55) << 1);

        for (int i = 0; i < maskArray.length; i++) {
            maskArray[i] = (maskLong & 1) == 1;
            maskLong = maskLong >> 1;
        }

        return SPECIES_I256.loadMask(maskArray, 0);
    }

    private VectorMask<Integer> getMaskEven(VectorMask<Integer> mask) {
        long maskLong = mask.toLong();
        maskLong = (maskLong & 0x2A) | ((maskLong & 0x2A) << 1);

        for (int i = 0; i < maskArray.length; i++) {
            maskArray[i] = (maskLong & 1) == 1;
            maskLong = maskLong >> 1;
        }

        return SPECIES_I256.loadMask(maskArray, 0);
    }
}
