package org.forza.transport;

import org.forza.common.Url;
import org.forza.common.exception.RemotingException;
import org.forza.reomoting.Connection;

public interface Client extends Endpoint {

    Connection ctreateConnectionIfAbsent(Url url) throws RemotingException;

    <T> T request(Url url, Object request) throws RemotingException;

    <T> T request(Object request) throws RemotingException;

}
