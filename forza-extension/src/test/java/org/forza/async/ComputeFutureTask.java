package org.forza.async;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程 异步计算任务结果
 */
public class ComputeFutureTask<V> extends FutureTask<V> {
    private AtomicInteger hasRun = new AtomicInteger();

    public ComputeFutureTask(Callable<V> callable) {
        super(callable);
    }

    @Override
    public void run() {
        int i = hasRun.getAndIncrement();
        System.out.println("执行次数：" + i);
        super.run();
    }
}
