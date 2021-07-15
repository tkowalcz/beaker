package pl.tkowalcz;

import org.agrona.SystemUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.DTraceAsmProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pl.tkowalcz.varints.BlogPostVarIntEncoder;
import pl.tkowalcz.varints.VarIntsMicrobenchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
public class MulByConstantMicrobenchmark {

    private int[] inputs;
    private long[] outputs;

    @Setup
    public void setUp() {
        inputs = new Random(0)
                .ints(128_000)
                .toArray();

        outputs = new long[128_000];
    }

    @Benchmark
    public long[] ashiftAdd() {
        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = MulByHour.mulByHourMillis2(inputs[i]);
        }

        return outputs;
    }

    @Benchmark
    public long[] mul() {
        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = TimeUnit.HOURS.toMillis(inputs[i]);
        }

        return outputs;
    }

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        CommandLineOptions commandLineOptions = new CommandLineOptions(args);
        Options opt = new OptionsBuilder().parent(commandLineOptions)
                .include(MulByConstantMicrobenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
