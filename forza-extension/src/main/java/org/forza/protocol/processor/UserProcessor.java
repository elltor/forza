package org.forza.protocol.processor;

import org.forza.common.command.CommandCode;
import org.forza.common.extension.Extension;

import java.util.concurrent.ExecutorService;

/**
 * @Author:  
 * @DateTime: 2020/4/19
 * @Description: TODO
 */
@Extension
public interface UserProcessor<T> {

    String interest();

    CommandCode cmdCode();

    ExecutorService getExecutor();

    boolean processInIOThread();

    Object handleRequest(T request) throws Exception;
}
