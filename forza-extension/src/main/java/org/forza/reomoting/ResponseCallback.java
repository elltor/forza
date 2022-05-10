package org.forza.reomoting;

import org.forza.common.Invocation;

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
