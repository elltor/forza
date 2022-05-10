package org.forza.config;

import org.forza.common.Constants;

public class ForzaRemotingOption<T> extends ForzaOption<T> {
    public static final ForzaOption<String> SERIALIZATION = valueOf(ForzaRemotingOption.class, Constants.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION);
    protected ForzaRemotingOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
