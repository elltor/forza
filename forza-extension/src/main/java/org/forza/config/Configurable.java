package org.forza.config;

/**
 * 配置
 */
public interface Configurable {

    <T> Configurable option(ForzaOption<T> option, T value);

    <T> T option(ForzaOption<T> option);

}
