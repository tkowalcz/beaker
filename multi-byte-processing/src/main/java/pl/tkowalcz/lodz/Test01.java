package pl.tkowalcz.lodz;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.concurrent.ThreadLocalRandom;

public class Test01 {

    public static void main(String[] args) {
        int[] inputArray = ThreadLocalRandom.current()
                .ints(1024 * 1024 * 256, 0, 100)
                .toArray();

        long sum = 0;

        VectorSpecies<Integer> species = IntVector.SPECIES_PREFERRED;

        System.out.println("species.length() = " + species.length());
        System.out.println("species.elementSize() = " + species.elementSize());
        System.out.println("species.elementType() = " + species.elementType());


        for (int i = 0; i < inputArray.length; i += species.length()) {
            IntVector vector = IntVector.fromArray(species, inputArray, i);

            sum += vector.reduceLanesToLong(VectorOperators.ADD);
        }

        System.out.println("sum = " + sum);
    }
}
