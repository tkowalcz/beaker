package pl.tkowalcz.meetup1;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SelectWhere {

    public static void main(String[] args) throws IOException {
        byte[] data = Files.toByteArray(new File(args[0]));
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int count = 0;
        int below = 0;

        while (buffer.hasRemaining()) {
            int id = buffer.getInt();
            int value = buffer.getInt();

            count++;
            if (value < 150) {
                below++;
            }
        }

        System.out.println("count = " + count);
        System.out.println("below = " + below);
    }
}
