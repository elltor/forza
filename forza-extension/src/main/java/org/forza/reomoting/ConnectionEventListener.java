package org.forza.reomoting;

import org.forza.common.enums.ConnectionEventType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接事件监听器
 */
public class ConnectionEventListener {

    /** 连接类型处理器 */
    private ConcurrentHashMap<ConnectionEventType, List<ConnectionEventProcessor>> processors =
            new ConcurrentHashMap<>();

    /**
     * 事件触发
     *
     * @param type
     * @param connection
     */
    public void onEvent(ConnectionEventType type, Connection connection) {
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if (processorList != null) {
            for (ConnectionEventProcessor processor : processorList) {
                processor.onEvent(connection);
            }
        }
    }

    /**
     * 添加连接处理器
     *
     * @param type      连接事件类型
     * @param processor 处理器
     */
    public void addConnectionEventProcessor(ConnectionEventType type,
                                            ConnectionEventProcessor processor) {
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if (processorList == null) {
            this.processors.putIfAbsent(type, new ArrayList<ConnectionEventProcessor>(1));
            processorList = this.processors.get(type);
        }
        processorList.add(processor);
    }
}
