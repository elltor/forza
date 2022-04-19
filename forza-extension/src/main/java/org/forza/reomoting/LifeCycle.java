package org.forza.reomoting;

import org.forza.common.exception.LifeCycleException;

/**
 * 生命周期
 *
 * @Author:  
 * @DateTime: 2020/3/18
 * @Description: TODO
 */
public interface LifeCycle {

    void startUp() throws LifeCycleException;

    void shutDown() throws LifeCycleException;

    boolean isStarted();
}
