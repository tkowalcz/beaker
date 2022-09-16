package pl.tkowalcz.sort;

import jdk.incubator.vector.IntVector;

public class SortedArraysUnion {

    private static final int[] INVALID = new int[]{0, 0};
    private static final int[] CHOOSE_RIGHT = new int[]{0, 0xFFFFFFFF};
    private static final int[] CHOOSE_LEFT = new int[]{0xFFFFFFFF, 0};

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    /////////////////////////////////
    // LEFT/RIGHT | 0 | 1 |
    //      0     | x | R |
    //      1     | L | ~ |

    private static final int[][][] DECISIONS = {
            {
                    INVALID, CHOOSE_RIGHT
            },
            {
                    CHOOSE_LEFT, CHOOSE_LEFT
            }
    };

    private static final long COMPARISON_MASK = 0x7F_FF_FF_FF_7F_FF_FF_FFL;
    private static final long ADD_ONE = 0x00_00_00_01_00_00_00_01L;

    public void simpleUnion(int[] leftArray, int[] rightArray, int[] outputArray) {
        int leftPointer = 0;
        int rightPointer = 0;
        int outputPointer = 0;

        while (leftPointer < leftArray.length && rightPointer < rightArray.length) {
            long comparisonResult = leftArray[leftPointer] - rightArray[rightPointer];

            if (comparisonResult < 0) {
                outputArray[outputPointer] = leftArray[leftPointer];
                leftPointer++;
            } else if (comparisonResult == 0) {
                outputArray[outputPointer] = leftArray[leftPointer];
                leftPointer++;
                rightPointer++;
            } else {
                outputArray[outputPointer] = rightArray[rightPointer];
                rightPointer++;
            }

            outputPointer++;
        }

        while (leftPointer < leftArray.length) {
            outputArray[outputPointer] = leftArray[leftPointer];

            outputPointer++;
            leftPointer++;
        }

        while (rightPointer < rightArray.length) {
            outputArray[outputPointer] = rightArray[rightPointer];

            outputPointer++;
            rightPointer++;
        }
    }

    public void swarUnion(int[] leftArray, int[] rightArray, int[] outputArray) {
        int leftPointer = 0;
        int rightPointer = 0;
        int outputPointer = 0;

        while (leftPointer < leftArray.length && rightPointer < rightArray.length) {
            int leftValue = leftArray[leftPointer];
            int rightValue = rightArray[rightPointer];

            long v1 = (((long) rightValue) << 32) | leftValue;
            long v2 = (((long) leftValue) << 32) | rightValue;

            long leftIsLessOrEqual = ((v2 + (v1 ^ COMPARISON_MASK)) + ADD_ONE) & ~COMPARISON_MASK;
            int leftIndex = ((int) leftIsLessOrEqual) >>> 31;
            int rightIndex = (int) (leftIsLessOrEqual >>> 63);

            leftPointer += leftIndex;
            rightPointer += rightIndex;

            int[] masks = DECISIONS[leftIndex][rightIndex];
            outputArray[outputPointer] = (leftValue & masks[LEFT]) + (rightValue & masks[RIGHT]);

            outputPointer++;
        }

        while (leftPointer < leftArray.length) {
            outputArray[outputPointer] = leftArray[leftPointer];

            outputPointer++;
            leftPointer++;
        }

        while (rightPointer < rightArray.length) {
            outputArray[outputPointer] = rightArray[rightPointer];

            outputPointer++;
            rightPointer++;
        }
    }

    ///////////////////////////////////////
    // LEFT/RIGHT | 0 0 | 0 1 | 1 0 | 1 1 |
    //      0 0   | xxx |     |     |     |
    //      0 1   |     |     |     |     |
    //      1 0   |     |     |     |     |
    //      1 1   |     |     |     |     |

    private static final int[][][] DECISIONS_2x2 = {
            {
                    INVALID, CHOOSE_RIGHT
            },
            {
                    CHOOSE_LEFT, CHOOSE_LEFT
            }
    };

    public void swarUnion2x2(int[] leftArray, int[] rightArray, int[] outputArray) {
        int leftPointer = 0;
        int rightPointer = 0;
        int outputPointer = 0;

        IntVector[] sortedVectors = new IntVector[2];

        IntVector leftVector = IntVector.fromArray(IntVector.SPECIES_256, leftArray, leftPointer);
        IntVector rightVector = IntVector.fromArray(IntVector.SPECIES_256, rightArray, rightPointer);
        while (leftPointer < leftArray.length && rightPointer < rightArray.length) {
            DoubleVectorBitonicSort.sort(leftVector, rightVector, sortedVectors);

            sortedVectors[0].intoArray(outputArray, outputPointer);
            outputPointer += IntVector.SPECIES_128.length();

            if (leftArray[leftPointer] < rightArray[rightPointer]) {
                leftVector = IntVector.fromArray(IntVector.SPECIES_256, leftArray, leftPointer);
                leftPointer += IntVector.SPECIES_128.length();
            } else {
                rightVector = IntVector.fromArray(IntVector.SPECIES_256, rightArray, rightPointer);
                rightPointer += IntVector.SPECIES_128.length();
            }
        }

        while (leftPointer < leftArray.length) {
            outputArray[outputPointer] = leftArray[leftPointer];

            outputPointer++;
            leftPointer++;
        }

        while (rightPointer < rightArray.length) {
            outputArray[outputPointer] = rightArray[rightPointer];

            outputPointer++;
            rightPointer++;
        }
    }
}
