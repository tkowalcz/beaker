package pl.tkowalcz.sort;

public class Histogram {

    private final int[] level0 = new int[256];
    private final int[] level1 = new int[256];
    private final int[] level2 = new int[256];
    private final int[] level3 = new int[256];

    public void calculate(int[] input) {
        for (int value : input) {
            value -= Integer.MIN_VALUE;
            level0[(value & 0xFF)]++;
            level1[((value >>> 8) & 0xFF)]++;
            level2[((value >>> 16) & 0xFF)]++;
            level3[((value >>> 24) & 0xFF)]++;
        }
    }

    public int[] getLevel0() {
        return level0;
    }

    public int[] getLevel1() {
        return level1;
    }

    public int[] getLevel2() {
        return level2;
    }

    public int[] getLevel3() {
        return level3;
    }
}
