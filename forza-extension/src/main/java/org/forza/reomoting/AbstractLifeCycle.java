package org.forza.reomoting;

import org.forza.common.exception.LifeCycleException;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractLifeCycle implements LifeCycle {

    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void startUp() {
        if (isStarted.compareAndSet(false, true)) {
            return;
        }
        throw new LifeCycleException("This component has started");

    }

    @Override
    public void shutDown() {
        if (isStarted.compareAndSet(true, false)) {
            return;
        }
        throw new LifeCycleException("This component has shutdown");
    }


    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

}
