package pl.tkowalcz.meetup1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectWhereMicrobenchmarkTest {

    @Test
    public void plainLoopTest() {
        // Given
        SelectWhereMicrobenchmark microbenchmark = new SelectWhereMicrobenchmark();
        microbenchmark.setUp();

        // When
        int actual = microbenchmark.plainLoop();

        // Then
        System.out.println("actual = " + actual);
    }
}
