package org.forza.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;
import org.forza.common.Url;
import org.forza.common.buffer.ChannelBuffer;
import org.forza.common.command.RemotingCommand;
import org.forza.reomoting.Connection;

import java.util.List;

/**
 * @Author:  
 * @DateTime: 2020/4/5
 * @Description: TODO
 */
public final class CodecAdapter {
    private final Codec codec;
    @Getter
    private final InternalDecoder decoder = new InternalDecoder();
    @Getter
    private final InternalEncoder encoder = new InternalEncoder();
    private Url url;

    public CodecAdapter(Codec codec, Url url) {
        this.codec = codec;
        this.url = url;
    }

    private class InternalDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            ChannelBuffer buffer = new ChannelBuffer(in);
            Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
            try {
                do {
                    int saveReadIndex = buffer.readerIndex();
                    Object msg = codec.decode(connection, buffer);
                    if (Codec.DecodeResult.NEED_MORE_INPUT.equals(msg)) {
                        // 重置读指针
                        buffer.readerIndex(saveReadIndex);
                        break;
                    } else {
                        if (msg != null) {
                            out.add(msg);
                        }
                    }
                } while (buffer.readable());

            } finally {
                // 防止内存泄漏
                Connection.removeChannelIfDisconnected(ctx.channel());
            }
        }
    }

    private class InternalEncoder extends MessageToByteEncoder<RemotingCommand> {

        @Override
        protected void encode(ChannelHandlerContext ctx, RemotingCommand cmd, ByteBuf out) throws Exception {
            ChannelBuffer buffer = new ChannelBuffer(out);
            Connection connection = Connection.getOrAddConnection(ctx.channel(), url);
            try {
                codec.encode(connection, buffer, cmd);
            } finally {
                Connection.removeChannelIfDisconnected(ctx.channel());
            }

        }
    }


}
