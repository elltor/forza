package org.forza.config;

import org.forza.common.Constants;

/**
 * @Author:  
 * @DateTime: 2020/4/10
 * @Description: TODO
 */
public class ForzaRemotingOption<T> extends ForzaOption<T> {
    public static final ForzaOption<String> SERIALIZATION = valueOf(ForzaRemotingOption.class, Constants.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION);
    protected ForzaRemotingOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
