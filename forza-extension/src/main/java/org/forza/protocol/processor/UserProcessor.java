package org.forza.protocol.processor;

import org.forza.common.command.CommandCode;
import org.forza.common.extension.Extension;

import java.util.concurrent.ExecutorService;

@Extension
public interface UserProcessor<T> {

    String interest();

    CommandCode cmdCode();

    ExecutorService getExecutor();

    boolean processInIOThread();

    Object handleRequest(T request) throws Exception;
}
