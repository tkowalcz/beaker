package pl.tkowalcz;

import xerial.jnuma.Numa;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * TODO: USE JMH
 */
public class NUMABandwidth {

    private static ByteBuffer[] dataOnNodes;

    public static void main(String[] args) {
        String capacityString = args[0];

        int capacity = Integer.parseInt(capacityString) * 1024 * 1024;
        System.out.println("capacity = " + capacity);
        System.out.println("NUMA is " + (Numa.isAvailable() ? "available" : "not available"));

        int numNodes = Numa.numNodes();
        System.out.println("numNodes = " + numNodes);

        Numa.runOnNode(0);
        System.out.println("Running on NUMA node 0");

        dataOnNodes = new ByteBuffer[numNodes];
        for (int i = 0; i < numNodes; i++) {
            ByteBuffer byteBuffer = Numa.allocOnNode(capacity, i);

            new Random(0)
                    .ints()
                    .limit(capacity / 4)
                    .forEach(byteBuffer::putInt);

            byteBuffer.flip();
            dataOnNodes[i] = byteBuffer;
        }

        for (int i = 0; i < numNodes; i++) {
            long start = System.nanoTime();

            int result = 0;
            ByteBuffer byteBuffer = dataOnNodes[i];

            for (int j = 0; j < byteBuffer.limit(); j += 8) {
                result += byteBuffer.getLong(j);
            }

            long end = System.nanoTime();

            long elapsedTimeMicros = TimeUnit.NANOSECONDS.toMillis(end - start);
            double bandwidthGB = (1000.0 / elapsedTimeMicros * capacity) / 1024 / 1024 / 1024;
            System.out.println("Sequential access time from node " + i + " is " + elapsedTimeMicros + "ms. " + result + " (" + bandwidthGB + "GB/s)");
        }

        for (int i = 0; i < numNodes; i++) {
            long start = System.nanoTime();

            ByteBuffer byteBuffer = dataOnNodes[i];

            long result = new Random(0)
                    .ints(0, byteBuffer.limit())
                    .limit(byteBuffer.limit() / 8)
                    .mapToLong(byteBuffer::getLong)
                    .sum();

            long end = System.nanoTime();

            long elapsedTimeMicros = TimeUnit.NANOSECONDS.toMillis(end - start);
            double bandwidthGB = (1000.0 / elapsedTimeMicros * capacity) / 1024 / 1024;
            System.out.println("Random access time from node " + i + " is " + elapsedTimeMicros + "ms. " + result + " (" + bandwidthGB + "MB/s)");
        }
    }
}
