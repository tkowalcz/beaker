package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.apache.commons.lang3.tuple.Pair;

public class IntVectorHistogram {

    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_256;

    // 16 lanes of AVX512 ints
    private static final int[] BUCKET_BOUNDS = {};

    public static Pair<Integer, Integer> adaptiveHistogram(int[] array) {
        Pair<Integer, Integer> range = getRange(array);
        IntVector buckets = createBuckets(range);

        IntVector ones = IntVector.broadcast(SPECIES, 1);
        for (int i = 0; i < array.length; i++) {
            IntVector value = IntVector.broadcast(SPECIES, array[i]);

            VectorMask<Integer> lessThan = value.lt(array[i]);
        }

        return range;
    }

    private static IntVector createBuckets(Pair<Integer, Integer> range) {
        int bucketSize = (range.getRight() - range.getLeft()) / SPECIES.length();
        IntVector buckets = IntVector.broadcast(SPECIES, range.getLeft());

        long maskLong = 0xFFFF_FFFFL;
        for (int i = 0; i < SPECIES.length() - 1; i++) {
            maskLong <<= 1;
            buckets = buckets.add(bucketSize, VectorMask.fromLong(SPECIES, maskLong));
        }

        return buckets;
    }

    private static Pair<Integer, Integer> getRange(int[] array) {
        IntVector minAccumulator = IntVector.broadcast(SPECIES, Integer.MAX_VALUE);
        IntVector maxAccumulator = IntVector.broadcast(SPECIES, Integer.MIN_VALUE);

        for (int i = 0; i < SPECIES.loopBound(array.length); i += SPECIES.length()) {
            IntVector value = IntVector.fromArray(SPECIES, array, i);

            minAccumulator = minAccumulator.min(value);
            maxAccumulator = maxAccumulator.max(value);
        }

        int min = minAccumulator.reduceLanes(VectorOperators.MIN);
        int max = maxAccumulator.reduceLanes(VectorOperators.MAX);

        return Pair.of(min, max);
    }

}
