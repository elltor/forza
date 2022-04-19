package org.forza.protocol;

import org.forza.common.command.CommandCode;
import org.forza.protocol.handler.CommandHandler;

import java.util.concurrent.ExecutorService;

/**
 * 协议接口
 */
public interface Protocol {

    /**
     * 获取命令处理器
     */
    CommandHandler getCommandHandler(CommandCode cmdCode);

    /**
     * 获取执行器
     *
     * @return ExecutorService
     */
    ExecutorService getDefaultExecutor();

    /**
     * 设置执行器
     *
     * @param executor ExecutorService
     */
    void setExecutor(ExecutorService executor);

}
