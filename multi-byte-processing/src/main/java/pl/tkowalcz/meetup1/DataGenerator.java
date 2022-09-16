package pl.tkowalcz.meetup1;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

@Command(name = "DataGenerator", mixinStandardHelpOptions = true, version = "1.0",
        description = "Generates random database data in row or columnar format")
public class DataGenerator implements Callable<Integer> {

    @Option(names = {"-f", "--format"}, description = "record organisation: column or row")
    private DataFormat formatString;

    @Option(names = {"-m", "--max"}, description = "max value in the generated dataset")
    private int maxValue;

    @Option(names = {"-t", "--threshold"}, description = "threshold value")
    private int thresholdValue;

    @Option(names = {"-p", "--prob"}, description = "probability of record value being over threshold value")
    private double probability;

    @Option(names = {"-r", "--records"}, description = "output size in records")
    private int sizeRecords;

    @Option(names = {"-o", "--file"}, description = "output file")
    private String outputFile;

    public static void main(String[] args) throws IOException {
        int exitCode = new CommandLine(new DataGenerator()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws IOException {
        try (OutputStream writer = Files.newOutputStream(
                Paths.get(outputFile),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            ThreadLocalRandom random = ThreadLocalRandom.current();

            random.doubles()
                    .limit(sizeRecords)
                    .mapToInt(value -> value < probability / 100.0 ? random.nextInt(thresholdValue) : random.nextInt(thresholdValue, maxValue))
//                    .peek(System.out::println)
                    .forEach(value -> {
                        try {
                            writer.write(Ints.toByteArray(random.nextInt()));
                            writer.write(Ints.toByteArray(value));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }

        return 0;
    }
}
