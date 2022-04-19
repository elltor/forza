package org.forza.reomoting;

import org.forza.common.Invocation;

/**
 * @Author:  
 * @DateTime: 2020/4/28
 * @Description: TODO
 */
public interface ResponseCallback {

    /**
     * done.
     *
     * @param invocation
     */
    void done(Invocation invocation);

    /**
     * caught exception.
     *
     * @param throwable
     */
    void caught(Throwable throwable);
}
