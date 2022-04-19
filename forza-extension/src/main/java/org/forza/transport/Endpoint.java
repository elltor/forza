package org.forza.transport;

import org.forza.common.Url;

import java.net.InetSocketAddress;

/**
 * 终端
 *
 * @Author:  
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
public interface Endpoint {

    void close();

    Url getUrl();

    InetSocketAddress getLocalAddress();

    boolean isServerSide();
}
