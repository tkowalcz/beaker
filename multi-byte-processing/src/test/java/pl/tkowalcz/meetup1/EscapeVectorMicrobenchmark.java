package pl.tkowalcz.meetup1;

import jdk.incubator.vector.*;
import org.agrona.SystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "-XX:+UnlockDiagnosticVMOptions",
        "-XX:+PrintCompilation",
//        "-XX:-UseSuperWord",
        "-XX:CompileCommand=print,*.vectorized",
//        "-XX:CompileCommand=print,*.xorJava",
        "-XX:PrintAssemblyOptions=intel",
//        "-XX:+UseEpsilonGC",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+EnableVectorSupport",
        "-XX:+EnableVectorReboxing",
        "-XX:+EnableVectorAggressiveReboxing",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
})
public class EscapeVectorMicrobenchmark {

    @Param({"C:\\Program Files\\Internet Explorer\\ie6.exe"})
    private String input;

    private byte[] dataSequential;
    private byte[] dataVector;

    private byte[] outputVector;

    @Setup
    public void setUp() {
        System.out.println("ByteVector.SPECIES_PREFERRED.length() = " + ByteVector.SPECIES_PREFERRED.length());
        dataSequential = input.getBytes(StandardCharsets.US_ASCII);

        int pad = 92;//input.length() + 2 * ByteVector.SPECIES_PREFERRED.length() - input.length() % ByteVector.SPECIES_PREFERRED.length() +1;
        dataVector = StringUtils.rightPad(input, pad, ' ').getBytes(StandardCharsets.US_ASCII);

        outputVector = new byte[1024];
    }

    //    @Benchmark
    public byte[] sequential() {
        EscapeVector.escape(dataSequential, outputVector);
        return outputVector;
    }

    @Benchmark
    public byte[] vectorized() {
        EscapeVector.vectorEscape(dataVector, outputVector);
        return outputVector;
    }

    public static void main(String[] args) throws RunnerException {
        String profilerParams;
        if (SystemUtil.osName().toLowerCase().startsWith("mac os")) {
            profilerParams = "event=cpu;libPath=/Users/tkowalcz/Downloads/async-profiler-1.8.5-macos-x64/build/libasyncProfiler.dylib";
        } else {
            profilerParams = "event=cpu;libPath=/home/ec2-user/async-profiler-1.8.8-linux-x64/build/libasyncProfiler.so";
        }

        Options opt = new OptionsBuilder()
//                .param("prof")
                .include(EscapeVectorMicrobenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
//                .resultFormat(ResultFormatType.CSV)
//                .addProfiler(AsyncProfiler.class, profilerParams)
                .addProfiler(LinuxPerfProfiler.class)
                .threads(1)
                .build();

        new Runner(opt).run();
    }
}
