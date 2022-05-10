package org.forza.reomoting;

import org.forza.common.exception.LifeCycleException;

public interface LifeCycle {

    void startUp() throws LifeCycleException;

    void shutDown() throws LifeCycleException;

    boolean isStarted();
}
