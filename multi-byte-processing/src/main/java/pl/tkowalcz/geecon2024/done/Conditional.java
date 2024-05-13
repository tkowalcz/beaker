package pl.tkowalcz.geecon2024.done;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

public class Conditional {

    public static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    public int selectWhere(int[] sales) {
        int count = 0;
        for (int i = 0; i < sales.length; i++) {
            if (sales[i] > 0) {
                count++;
            }
        }

        return count;
    }

    public int selectWhereVectorised(int[] sales) {
        IntVector result = IntVector.zero(SPECIES);

        int loopBound = SPECIES.loopBound(result.length());
        for (int i = 0; i < loopBound; i += SPECIES.length()) {
            IntVector data = IntVector.fromArray(SPECIES, sales, i);


        }

        return 0;
    }
}
