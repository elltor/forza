package org.forza.transport;

import org.forza.common.Url;

import java.net.InetSocketAddress;

/**
 * 终端
 */
public interface Endpoint {

    void close();

    Url getUrl();

    InetSocketAddress getLocalAddress();

    boolean isServerSide();
}
