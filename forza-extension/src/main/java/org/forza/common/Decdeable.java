package org.forza.common;

import io.netty.handler.codec.DecoderException;

/**
 * @Author:  
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
public interface Decdeable {

    void decodeClassName() throws DecoderException;

    void decodeData() throws DecoderException;

}
