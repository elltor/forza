package org.forza.protocol.processor;

import org.forza.common.command.CommandCode;
import org.forza.common.enums.CommandCodeEnum;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.ExecutorService;

/**
 * @Author:  
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public abstract class AbstractUserProcessorAdapter<T> implements UserProcessor<T> {

    private ExecutorService executor;

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public boolean processInIOThread() {
        return false;
    }

    @Override
    public CommandCode cmdCode() {
        return CommandCodeEnum.GENERAL_CMD;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public String interest() {
        Class<T> interestClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return interestClazz.getName();
    }
}
