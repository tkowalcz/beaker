package pl.tkowalcz.geecon2024;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;

import static pl.tkowalcz.geecon2024.Microbenchmark.arrayOf;

public class HelloVectors {

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    private static final int TEST_ARRAY_SIZE = 1025;

    public static void hello(double[] input1) {

    }

    public static void main(String[] args) {
        double[] array1 = arrayOf(TEST_ARRAY_SIZE, 1.0);
        double[] array2 = arrayOf(TEST_ARRAY_SIZE, 2.0);
        double[] result = new double[array1.length];

        hello(array1);
//        add(array1, array2, result);
//        System.out.println(Arrays.toString(result));
    }
}
