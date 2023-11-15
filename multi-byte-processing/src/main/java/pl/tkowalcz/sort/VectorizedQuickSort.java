package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.apache.commons.lang3.mutable.MutableInt;

public class VectorizedQuickSort {

    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_256;

    static final int LANES_COUNT = SPECIES.length();
    private static final int FALLBACK_SIZE = SPECIES.length() * 2;

    @SuppressWarnings("unchecked")
    private static final VectorMask<Integer>[] COMPRESS_MASKS = new VectorMask[]{
            SPECIES.loadMask(new boolean[]{false, false, false, false, false, false, false, false}, 0),
            SPECIES.loadMask(new boolean[]{true, false, false, false, false, false, false, false}, 0),
            SPECIES.loadMask(new boolean[]{true, true, false, false, false, false, false, false}, 0),
            SPECIES.loadMask(new boolean[]{true, true, true, false, false, false, false, false}, 0),
            SPECIES.loadMask(new boolean[]{true, true, true, true, false, false, false, false}, 0),
            SPECIES.loadMask(new boolean[]{true, true, true, true, true, false, false, false}, 0),
            SPECIES.loadMask(new boolean[]{true, true, true, true, true, true, false, false}, 0),
            SPECIES.loadMask(new boolean[]{true, true, true, true, true, true, true, false}, 0),
            SPECIES.loadMask(new boolean[]{true, true, true, true, true, true, true, true}, 0),
    };

    public static void sort(int[] array) {
        sort(array, 0, array.length - 1);
    }

    private static void sort(int[] array, int leftIndex, int rightIndex) {
        if (rightIndex - leftIndex <= FALLBACK_SIZE) {
//            IntVectorBitonicSort.sortElementsStartingFrom(array, leftIndex, rightIndex);
        } else {
            int pivotIndex = selectPivotIndex(array, leftIndex, rightIndex);
            swap(array, pivotIndex, rightIndex);

            int pivotValue = array[rightIndex];
            int partitionBound = partition(array, leftIndex, rightIndex, pivotValue);
            swap(array, partitionBound, rightIndex);

            sort(array, leftIndex, partitionBound - 1);
            sort(array, partitionBound + 1, rightIndex);
        }
    }

    private static int selectPivotIndex(int[] input, int leftIndex, int rightIndex) {
        return leftIndex + (rightIndex - leftIndex) / 2;
    }

    public static int partition(
            int[] array,
            int leftIndex,
            int rightIndex,
            int pivotValue
    ) {
        IntVector pivotVec = IntVector.broadcast(SPECIES, pivotValue);

        IntVector leftVec = IntVector.fromArray(SPECIES, array, leftIndex);
        MutableInt left_w = new MutableInt(leftIndex);
        leftIndex = leftIndex + LANES_COUNT;

        MutableInt right_w = new MutableInt(rightIndex);
        rightIndex = rightIndex - LANES_COUNT;
        IntVector rightVec = IntVector.fromArray(SPECIES, array, rightIndex);

        IntVector value;
        while (leftIndex + LANES_COUNT <= rightIndex) {
            if (leftIndex - left_w.intValue() <= right_w.intValue() - rightIndex) {
                value = IntVector.fromArray(SPECIES, array, leftIndex);
                leftIndex += LANES_COUNT;
            } else {
                rightIndex -= LANES_COUNT;
                value = IntVector.fromArray(SPECIES, array, rightIndex);
            }

            partitionVectors(array, value, pivotVec, left_w, right_w);
        }

        partitionRemaining(array, pivotVec, leftIndex, rightIndex, left_w, right_w);
        partitionVectors(array, leftVec, pivotVec, left_w, right_w);
        partitionVectors(array, rightVec, pivotVec, left_w, right_w);

        return left_w.intValue();
    }

    static void partitionRemaining(int[] array, IntVector pivotVec, int leftIndex, int rightIndex, MutableInt left_w, MutableInt right_w) {
        int remaining = rightIndex - leftIndex;

        IntVector value = IntVector.fromArray(SPECIES, array, leftIndex);

        VectorMask<Integer> leMask = value.compare(VectorOperators.LE, pivotVec);
        VectorMask<Integer> maskLow = leMask.and(COMPRESS_MASKS[remaining]);
        VectorMask<Integer> maskHigh = leMask.not().and(COMPRESS_MASKS[remaining]);

        int nb_low = maskLow.trueCount();
        int nb_high = maskHigh.trueCount();

        value.compress(maskLow).intoArray(array, left_w.intValue(), COMPRESS_MASKS[nb_low]);
        left_w.add(nb_low);
        right_w.subtract(nb_high);
        value.compress(maskHigh).intoArray(array, right_w.intValue(), COMPRESS_MASKS[nb_high]);
    }

    static void partitionVectors(int[] array, IntVector value, IntVector pivotVec, MutableInt left_w, MutableInt right_w) {
        VectorMask<Integer> leMask = value.compare(VectorOperators.LE, pivotVec);
        VectorMask<Integer> gtMask = leMask.not();

        int nb_low = leMask.trueCount();
        int nb_high = LANES_COUNT - nb_low;

        value.compress(leMask).intoArray(array, left_w.intValue(), COMPRESS_MASKS[nb_low]);
        left_w.add(nb_low);
        right_w.subtract(nb_high);
        value.compress(gtMask).intoArray(array, right_w.intValue(), COMPRESS_MASKS[nb_high]);
    }

    private static void swap(int[] input, int index1, int index2) {
        int value = input[index1];
        input[index1] = input[index2];
        input[index2] = value;
    }
}
