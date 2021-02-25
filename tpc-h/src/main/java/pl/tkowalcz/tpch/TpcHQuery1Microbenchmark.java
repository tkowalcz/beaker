package pl.tkowalcz.tpch;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
public class TpcHQuery1Microbenchmark {

    private Database database;
    private TpcHQuery1 query1;

    @Setup
    public void setUp() {
        try {
            database = Bootstrap.createDatabase();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        query1 = new TpcHQuery1();
    }

    @Benchmark
    public ProcessingItem[] stream() {
        return query1.executeStream(database);
    }

    @Benchmark
    public ProcessingItem[] parallelStream() {
        return query1.executeParallelStream(database);
    }

    @Benchmark
    public List<ProcessingItem> rx() {
        return query1.executeRx(database);
    }

    @Benchmark
    public ProcessingItem[] forLoop() {
        return query1.executeForLoop(database);
    }

    @Benchmark
    public ProcessingItem[] columnar() {
        return query1.executeColumnar(database);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + TpcHQuery1Microbenchmark.class.getSimpleName() + ".*")
                        .warmupIterations(5)
                        .measurementIterations(5)
                        .addProfiler(GCProfiler.class)
                        .resultFormat(ResultFormatType.CSV)
                        .build();

        new Runner(opt).run();
    }
}
