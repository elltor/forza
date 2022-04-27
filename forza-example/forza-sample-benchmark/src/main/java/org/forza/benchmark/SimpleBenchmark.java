package org.forza.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 1, time = 2)
@Threads(4)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SimpleBenchmark {

    int cnt;

    // 初始化方法
    @Setup
    public void init() {
        cnt = 10;
    }

    @Benchmark
    public String test() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cnt; i ++) {
            sb.append(i + 1);
        }
        return sb.toString();
    }

    // 引导JMH启动
    public static void main(String[] args) throws Exception{
        Options op = new OptionsBuilder()
                .include(SimpleBenchmark.class.getSimpleName())
                .build();
        new Runner(op).run();
    }
}
