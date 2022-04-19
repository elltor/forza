package org.forza.codec;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.forza.common.buffer.ChannelBuffer;
import org.forza.common.command.RemotingCommand;
import org.forza.reomoting.Connection;

/**
 * 编解码器接口
 */
public interface Codec {

    /** 编码 */
    void encode(Connection connection, ChannelBuffer buffer, RemotingCommand msg) throws EncoderException;

    /** 解码 */
    Object decode(Connection connection, ChannelBuffer buffer) throws DecoderException;

    enum DecodeResult {
        NEED_MORE_INPUT
    }
}
