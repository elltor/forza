/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.forza.common.exception;

import org.forza.reomoting.Connection;

import java.net.InetSocketAddress;

public class TimeoutException extends RemotingException {

    public static final int CLIENT_SIDE = 0;
    public static final int SERVER_SIDE = 1;
    private static final long serialVersionUID = 3122966731958222692L;
    private final int phase;

    public TimeoutException(boolean serverSide, Connection connection, String message) {
        super(connection, message);
        this.phase = serverSide ? SERVER_SIDE : CLIENT_SIDE;
    }

    public TimeoutException(boolean serverSide, InetSocketAddress localAddress,
                            InetSocketAddress remoteAddress, String message) {
        super(localAddress, remoteAddress, message);
        this.phase = serverSide ? SERVER_SIDE : CLIENT_SIDE;
    }

    public int getPhase() {
        return phase;
    }

    public boolean isServerSide() {
        return phase == 1;
    }

    public boolean isClientSide() {
        return phase == 0;
    }

}