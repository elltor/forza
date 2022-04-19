package org.forza.reomoting;

import org.forza.common.Invocation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author:  
 * @DateTime: 2020/4/28
 * @Description: TODO
 */
public class FutureAdapter<V> extends CompletableFuture<V> {

    private final ResponseFuture future;

    public FutureAdapter(ResponseFuture future) {
        this.future = future;
        future.setCallback(new ResponseCallback() {
            @Override
            public void done(Invocation invocation) {
                Optional.ofNullable(invocation)
                        .ifPresent(inv -> {
                            FutureAdapter.this.complete((V)inv.getData());
                        });
            }

            @Override
            public void caught(Throwable throwable) {
                FutureAdapter.this.completeExceptionally(throwable);
            }
        });
    }

    public ResponseFuture getFuture() {
        return future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return super.isDone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get() throws InterruptedException, ExecutionException {
        try {
            return super.get();
        } catch (ExecutionException | InterruptedException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return super.get(timeout, unit);
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
