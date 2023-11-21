package pl.tkowalcz.lodz;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.concurrent.ThreadLocalRandom;

public class Test01Empty {

    public static void main(String[] args) {
        int[] inputArray = ThreadLocalRandom.current()
                .ints(1024 * 1024 * 256, 0, 100)
                .toArray();

        long sum = 0;

        // ...

        System.out.println("sum = " + sum);
    }
}
