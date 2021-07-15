package pl.tkowalcz;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class MulByHourTest {

    @Test
    public void testMultiplication() {
        long[] actual = new Random(0).ints(10_000)
                .mapToLong(MulByHour::mulByHourMillis2)
                .toArray();

        long[] expected = new Random(0).ints(10_000)
                .mapToLong(TimeUnit.HOURS::toMillis)
                .toArray();

        assertThat(actual).isEqualTo(expected);
    }
}
