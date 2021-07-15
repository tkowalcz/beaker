package pl.tkowalcz.sort;

import jdk.incubator.vector.*;

public class VectorBubbleSort {

    static final VectorSpecies<Integer> SPECIES_I256 = VectorSpecies.of(int.class, VectorShape.S_256_BIT);

    private static final VectorShuffle<Integer> SHUFFLE_ODD = SPECIES_I256.shuffleFromArray(new int[]{1, 0, 3, 2, 5, 4, 7, 6}, 0);
    private static final VectorShuffle<Integer> SHUFFLE_EVEN = SPECIES_I256.shuffleFromArray(new int[]{0, 2, 1, 4, 3, 6, 5, 7}, 0);

    private static final VectorMask<Integer> MASK_0x55 = SPECIES_I256.loadMask(new boolean[]{false, true, false, true, false, true, false, true}, 0);
    private static final VectorMask<Integer> MASK_0x2A = SPECIES_I256.loadMask(new boolean[]{true, false, true, false, true, false, true, false}, 0);

    private final boolean[] maskArray = new boolean[SPECIES_I256.length()];

    public IntVector sort(IntVector input) {
        for (int i = 0; i < 4; i++) {
            {
                IntVector rearrangedVector = input.rearrange(SHUFFLE_ODD);

                VectorMask<Integer> lessThan = rearrangedVector.compare(VectorOperators.LT, input);
                long maskLong = lessThan.toLong();
                maskLong = (maskLong & 0x55) | ((maskLong & 0x55) << 1);
                VectorMask<Integer> mask = VectorMask.fromLong(SPECIES_I256, maskLong);
//                lessThan = lessThan.and(MASK_0x55).or(lessThan.and(MASK_0x55));
//                VectorMask<Integer> integerMask = getMaskOdd(lessThan);
                input = input.blend(rearrangedVector, mask);
            }
            {
                IntVector rearrangedVector = input.rearrange(SHUFFLE_EVEN);

                VectorMask<Integer> lessThan = rearrangedVector.compare(VectorOperators.LT, input);
                long maskLong = lessThan.toLong();
                maskLong = (maskLong & 0x2A) | ((maskLong & 0x2A) << 1);
                VectorMask<Integer> mask = VectorMask.fromLong(SPECIES_I256, maskLong);

//                VectorMask<Integer> integerMask = getMaskEven(lessThan);
//                lessThan = lessThan.and(MASK_0x2A).or(lessThan.and(MASK_0x2A));
                input = input.blend(rearrangedVector, mask);
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
