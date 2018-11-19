/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package pl.tkowalcz;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xerial.jnuma.Numa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@State(Scope.Group)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class AllCorePingPongMicrobenchmark {

    public final AtomicBoolean flag = new AtomicBoolean();

    @State(Scope.Benchmark)
    public static class CoreSequence {

        @Param("0,0")
        public String coresConfigurations;

        public List<Integer> cores;

        @Setup
        public void setUp() {
            cores = Core2CoreDescriptor.fromString(coresConfigurations).toSynchronizedList();
            System.out.println("Will test following cores configuration: " + cores);
        }
    }

    @State(Scope.Thread)
    public static class CoreAssigner {

        @Setup
        public void setUp(CoreSequence coreSequence) {
            Integer core = coreSequence.cores.remove(0);

            Numa.runOnNode(core);
            System.out.println("Running on node: " + core);
        }
    }

    @Benchmark
    @Group("pingpong")
    public void ping(Control cnt, CoreAssigner coreAssigner) {
        while (!cnt.stopMeasurement && !flag.compareAndSet(false, true)) {
            // this body is intentionally left blank
        }
    }

    @Benchmark
    @Group("pingpong")
    public void pong(Control cnt, CoreAssigner coreAssigner) {
        while (!cnt.stopMeasurement && !flag.compareAndSet(true, false)) {
            // this body is intentionally left blank
        }
    }

    public static void main(String[] args) throws RunnerException {
        String[] coresConfigurations = IntStream.range(0, Numa.numNodes())
                .boxed()
                .flatMap(i -> IntStream.range(0, Numa.numNodes())
                        .mapToObj(__ -> new Core2CoreDescriptor(__, i)))
                .map(Core2CoreDescriptor::toString)
                .toArray(String[]::new);

        System.out.println("Will test following cores configurations: " + Arrays.toString(coresConfigurations));

        Options opt = new OptionsBuilder()
                .include(AllCorePingPongMicrobenchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(1)
                .param("coresConfigurations", coresConfigurations)
                .threads(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

class Core2CoreDescriptor {

    private int core1;
    private int core2;

    public Core2CoreDescriptor(int core1, int core2) {
        this.core1 = core1;
        this.core2 = core2;
    }

    @Override
    public String toString() {
        return core1 + "," + core2;
    }

    public List<Integer> toSynchronizedList() {
        return Collections.synchronizedList(new ArrayList<>(List.of(core1, core2)));
    }

    public static Core2CoreDescriptor fromString(String coresConfigurations) {
        String[] split = coresConfigurations.split(",");
        return new Core2CoreDescriptor(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
