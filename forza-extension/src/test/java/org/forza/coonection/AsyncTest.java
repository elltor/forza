package org.forza.coonection;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author:  
 * @DateTime: 2020/4/15
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class AsyncTest {

    @Test
    public void promiseTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EventExecutor executorA = new DefaultEventExecutor(new DefaultThreadFactory("EventA"));
        EventExecutor executorB = new DefaultEventExecutor();
        Channel channel = new NioSocketChannel();
        // 为EventLoop注册一个Promise
        Promise<Channel> newPromise = executorA.<Channel>newPromise();
        System.out.println(Thread.currentThread().getName());
        newPromise.addListener(f -> {
            if (f.isSuccess()) {
                Assert.assertEquals(channel, f.getNow());
                System.out.println(Thread.currentThread().getName());
                latch.countDown();
            }
        });
        Assert.assertEquals(false, executorB.inEventLoop());
        executorB.execute(new Runnable() {
            @Override
            public void run() {
                newPromise.setSuccess(channel);
            }
        });
        latch.await();
    }

    /**
     * 测试成功返回结果
     */
    @Test
    public void succeededFutureTest() {
        EventExecutor executor = new DefaultEventExecutor();
        Future<Boolean> future = executor.newSucceededFuture(Boolean.TRUE);
        if (future.isDone()) {
            Assert.assertEquals(true, future.getNow());
            Assert.assertEquals(true, future.isDone());
        }
    }

    /**
     * 测试取消定时任务
     */
    @Test
    public void scheduleTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EventExecutor executor = new DefaultEventExecutor();
        // 5s定时
        ScheduledFuture<?> schedule = executor.schedule(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, 5, TimeUnit.SECONDS);

        // 取消结果，也算完成，isDone() 返回 true
        schedule.cancel(false);

        Assert.assertEquals(true,schedule.isDone());
        Assert.assertEquals(true, schedule.isCancelled());

        // 如果成果取消则一直阻塞
        latch.await();
    }

    /**
     * 修改future的结果。future一旦执行成功或失败，操作不可被修改。
     * 下方线程执行完定时任务返回成果的future，已经完成，下方即使取消结果不会改变，Future#isCancelled() 仍为false
     */
    @Test
    public void changeFutureTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EventExecutor executor = new DefaultEventExecutor();
        // 5s定时
        ScheduledFuture<?> schedule = executor.schedule(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, 5, TimeUnit.SECONDS);

        // 等6s执行完成
        Thread.sleep(6 * 1000);

        // 结果定时任务。此操作也算完成执行，Future#isDone() 返回 true
        schedule.cancel(false);

        Assert.assertEquals(true,schedule.isDone());
        Assert.assertEquals(false, schedule.isCancelled());

        // 定时任务执行完成前阻塞
        latch.await();
    }

}
