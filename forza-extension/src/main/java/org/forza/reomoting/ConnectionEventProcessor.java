package org.forza.reomoting;

/**
 * 连接处理器接口
 */
@FunctionalInterface
public interface ConnectionEventProcessor {

    void onEvent(Connection connection);

}
