package pl.tkowalcz.geecon2024.done;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;
import pl.tkowalcz.MyBenchmark;
import pl.tkowalcz.geecon2024.Microbenchmark;

import java.util.Arrays;

import static pl.tkowalcz.geecon2024.Microbenchmark.arrayOf;

public class HelloVectors {

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    private static final int TEST_ARRAY_SIZE = 1025;

    public static void hello(double[] input1) {
        DoubleVector doubleVector = DoubleVector.fromArray(SPECIES, input1, 0);
        doubleVector = doubleVector.mul(doubleVector);

        System.out.println("SPECIES = " + SPECIES);
        System.out.println("doubleVector = " + doubleVector);
    }

    public static void add(double[] input1, double[] input2, double[] result) {
        int loopBound = SPECIES.loopBound(input1.length);
        int i = 0;
        for (; i < loopBound; i += SPECIES.length()) {
            VectorMask<Double> mask = SPECIES.indexInRange(i, input1.length);
            DoubleVector doubleVector1 = DoubleVector.fromArray(SPECIES, input1, i, mask);
            DoubleVector doubleVector2 = DoubleVector.fromArray(SPECIES, input2, i, mask);

            doubleVector1.add(doubleVector2).intoArray(result, i);
        }

        for (; i < input1.length; i++) {
            result[i] = input1[i] + input2[i];
        }
    }

    public static void main(String[] args) {
        double[] array1 = Microbenchmark.arrayOf(TEST_ARRAY_SIZE, 1.0);
        double[] array2 = arrayOf(TEST_ARRAY_SIZE, 2.0);
        double[] result = new double[array1.length];

        add(array1, array2, result);
        System.out.println(Arrays.toString(result));
    }
}
