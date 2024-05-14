package pl.tkowalcz.geecon2024.done;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import pl.tkowalcz.MyBenchmark;

public class Conditional {

    public static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    @MyBenchmark
    public static int selectWhere(int[] sales) {
        int count = 0;
        for (int i = 0; i < sales.length; i++) {
            if (sales[i] > 50) {
                count++;
            }
        }

        return count;
    }

    @MyBenchmark
    public static int selectWhereVectorised(int[] sales) {
        int count = 0;

        int i = 0;
        int loopBound = SPECIES.loopBound(sales.length);
        for (; i < loopBound; i += SPECIES.length()) {
            IntVector data = IntVector.fromArray(SPECIES, sales, i);

            VectorMask<Integer> comparisonResult = data.compare(VectorOperators.GT, 50);
            count += comparisonResult.trueCount();
        }

        for (; i < sales.length; i++) {
            if (sales[i] > 0) {
                count++;
            }
        }

        return count;
    }
}
