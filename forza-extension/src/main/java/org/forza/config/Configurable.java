package org.forza.config;

/**
 * 配置
 *
 * @Author:  
 * @DateTime: 2020/4/10
 * @Description: TODO
 */
public interface Configurable {

    <T> Configurable option(ForzaOption<T> option, T value);

    <T> T option(ForzaOption<T> option);

}
