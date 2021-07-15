package pl.tkowalcz.sort;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.ThreadLocalRandom;

public class VectorizedQuickSortTest {

    public static final int ARRAY_SIZE = 1024;

    private int[] inputArray;
    private int[] outputArray;

    @BeforeTest
    public void setUp() {
        inputArray = ThreadLocalRandom.current().ints(1024).toArray();
        outputArray = new int[ARRAY_SIZE];
    }

    @Test
    public void testName() {
        int pivot = inputArray[inputArray.length / 2];

        VectorizedQuickSort.sort(
                inputArray,
                outputArray,
                0,
                ARRAY_SIZE,
                pivot
        );
    }
}
