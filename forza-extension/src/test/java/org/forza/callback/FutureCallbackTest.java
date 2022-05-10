package org.forza.callback;

import org.forza.common.Invocation;
import org.forza.common.command.RequestCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.enums.CommandCodeEnum;
import org.forza.common.enums.ResponseStatus;
import org.forza.reomoting.Connection;
import org.forza.reomoting.DefaultFuture;
import org.forza.reomoting.FutureAdapter;
import org.forza.reomoting.ResponseCallback;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@RunWith(JUnit4.class)
public class FutureCallbackTest {

    @Test
    public void callbcak() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Channel channel = new NioSocketChannel();
        Connection connection = new Connection(channel);
        RequestCommand req = new RequestCommand(CommandCodeEnum.GENERAL_CMD);
        int id = req.getId();
        DefaultFuture future = DefaultFuture.newFuture(connection, req
                , 1000);

        future.setCallback(new ResponseCallback() {
            @Override
            public void done(Invocation response) {
                latch.countDown();

                System.out.println("callback");
            }


            @Override
            public void caught(Throwable exception) {
            }
        });
        ResponseCommand res = new ResponseCommand(id, CommandCodeEnum.GENERAL_CMD);
        res.setInvocation(new Invocation());
        res.setStatus(ResponseStatus.SUCCESS);
        DefaultFuture.received(connection, res);
        latch.await();
    }

    @Test
    public void callbackWithCom() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Channel channel = new NioSocketChannel();
        Connection connection = new Connection(channel);
        RequestCommand req = new RequestCommand(CommandCodeEnum.GENERAL_CMD);
        int id = req.getId();
        DefaultFuture future = DefaultFuture.newFuture(connection, req
                , 1000);
        FutureAdapter<String> futureAdapter = new FutureAdapter<String>(future);

        ResponseCommand res = new ResponseCommand(id, CommandCodeEnum.GENERAL_CMD);
        Invocation invocation = new Invocation();
        invocation.setData("---name---");
        res.setInvocation(invocation);
        res.setStatus(ResponseStatus.SUCCESS);
        DefaultFuture.received(connection, res);


        futureAdapter.whenComplete((k, v) -> {
            latch.countDown();
            System.out.println(k);
        });

        latch.await();

    }

    @Test
    public void completable_future_test() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            return "test";
        });

        completableFuture.whenComplete((k,v)->{
            System.out.println("finished");
        });
    }
}
