package org.forza.benchmark;

import com.forza.sample.api.SimpleRequestBody;
import org.forza.transport.Client;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * java -jar forza-example/forza-sample-benchmark/target/forza-benchmark.jar
 */
/* output

Benchmark                            Mode   Score       Units
RpcCallBenchmark.benchmark_null     thrpt   2.182       ops/ms
RpcCallBenchmark.benchmark_1k       thrpt   2.114       ops/ms
RpcCallBenchmark.benchmark_10k      thrpt   1.904       ops/ms
RpcCallBenchmark.benchmark_50k      thrpt   1.900       ops/ms

RpcCallBenchmark.benchmark_null     avgt    1.810       ms/op
RpcCallBenchmark.benchmark_1k       avgt    1.939       ms/op
RpcCallBenchmark.benchmark_10k      avgt    2.121       ms/op
RpcCallBenchmark.benchmark_50k      avgt    2.108       ms/op

// -----------------------------------------------------------

Benchmark                            Mode   Score       Units
RpcCallBenchmark.benchmark_null     thrpt   2.191       ops/ms
RpcCallBenchmark.benchmark_1k       thrpt   2.073       ops/ms
RpcCallBenchmark.benchmark_10k      thrpt   2.122       ops/ms
RpcCallBenchmark.benchmark_50k      thrpt   2.136       ops/ms

RpcCallBenchmark.benchmark_null     avgt    1.840       ms/op
RpcCallBenchmark.benchmark_1k       avgt    1.869       ms/op
RpcCallBenchmark.benchmark_10k      avgt    1.908       ms/op
RpcCallBenchmark.benchmark_50k      avgt    1.939       ms/op
 */

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 1, time = 10)
@Threads(4)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RpcCallBenchmark {

    private EndpointProvider endpointProvider;
    private Client client;
    private String str_1k;
    private String str_10k;
    private String str_50k;

    @Setup
    public void init() {
        endpointProvider = new EndpointProvider();
        client = endpointProvider.client();
        byte[] fill_1k = new byte[1024];
        Arrays.fill(fill_1k, (byte)'A');

        byte[] fill_10k = new byte[1024*10];
        Arrays.fill(fill_10k, (byte)'A');

        byte[] fill_50k = new byte[1024*50];
        Arrays.fill(fill_50k, (byte)'A');

        str_1k = new String(fill_1k);
        str_10k = new String(fill_10k);
        str_50k = new String(fill_50k);
    }

    @Benchmark
    public void benchmark_null() {
        SimpleRequestBody requestBody = new SimpleRequestBody(null, 0, 0L);
        client.request(requestBody);
    }

    @Benchmark
    public void benchmark_1k() {
        SimpleRequestBody requestBody = new SimpleRequestBody(str_1k, 0, 0L);
        client.request(requestBody);
    }

    @Benchmark
    public void benchmark_10k() {
        SimpleRequestBody requestBody = new SimpleRequestBody(str_10k, 0, 0L);
        client.request(requestBody);
    }

    @Benchmark
    public void benchmark_50k() {
        SimpleRequestBody requestBody = new SimpleRequestBody(str_10k, 0, 0L);
        client.request(requestBody);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(RpcCallBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
