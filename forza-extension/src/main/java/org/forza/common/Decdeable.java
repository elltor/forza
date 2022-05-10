package org.forza.common;

import io.netty.handler.codec.DecoderException;

public interface Decdeable {

    void decodeClassName() throws DecoderException;

    void decodeData() throws DecoderException;

}
