package org.forza.reomoting;

@FunctionalInterface
public interface ConnectionEventProcessor {

    void onEvent(Connection connection);

}
