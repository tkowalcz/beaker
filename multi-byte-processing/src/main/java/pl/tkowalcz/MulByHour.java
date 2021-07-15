package pl.tkowalcz;

import java.util.concurrent.TimeUnit;

public class MulByHour {

    public static long mulByHourMillis(long value) {
        long y = value << 7;
        long z = value << 12;
        long a = value << 16;

        return (value << 22) - ((value << 19) + a + z + y + y + y);
    }

    public static long by15_15(long value) {
        return (value << 8) - (value << 5) + value;
    }

    public static long mulByHourMillis2(long value) {
        long y = by15_15(value);
        long z = y << 7;

        return (z << 7) - z - z - z;
    }

    public static void main(String[] args) {
        long expected = TimeUnit.HOURS.toMillis(1) * 32;
        long actual = mulByHourMillis(32);

        System.out.println("expected = " + expected);
        System.out.println("actual   = " + actual);
    }
}
