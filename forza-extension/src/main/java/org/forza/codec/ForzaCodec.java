package org.forza.codec;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.forza.common.DecodeableInvocation;
import org.forza.common.Invocation;
import org.forza.common.Version;
import org.forza.common.buffer.ChannelBuffer;
import org.forza.common.buffer.ChannelBufferInputStream;
import org.forza.common.buffer.ChannelBufferOutputStream;
import org.forza.common.buffer.UnsafeByteArrayInputStream;
import org.forza.common.command.RemotingCommand;
import org.forza.common.command.RequestCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.enums.CommandCodeEnum;
import org.forza.common.enums.ResponseStatus;
import org.forza.reomoting.Connection;
import org.forza.serialization.ObjectInput;
import org.forza.serialization.ObjectOutput;
import org.forza.serialization.Serialization;
import org.forza.serialization.SerializationManager;
import org.forza.util.Bytes;
import org.forza.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author:  
 * @DateTime: 2020/4/5
 * @Description: TODO
 */
public class ForzaCodec extends AbstractCodec {
    private static final Logger logger = LoggerFactory.getLogger(ForzaCodec.class);
    protected static final int HEADER_LENGTH = 14;
    /** 1字节 魔数 */
    protected static final byte MAGIC = (byte) 0xad;
    /** 1字节 请求标志  */
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    /** 1字节 2way标志 */
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    /** 1字节 心跳检测标志 */
    protected static final byte FLAG_HEARTBEAT = (byte) 0x20;
    /** 4字节 序列化掩码 */
    protected static final int SERIALIZATION_MASK = 0x1f;
    /** 1字节 标志掩码 */
    protected static final byte FLAGS_MASK = 0x0;


    @Override
    public void encode(Connection con, ChannelBuffer buffer, RemotingCommand cmd) throws EncoderException {
        if (cmd instanceof RequestCommand) {
            encodeRequst(con, (RequestCommand) cmd, buffer);
        } else if (cmd instanceof ResponseCommand) {
            encodeResponse(con, (ResponseCommand) cmd, buffer);
        }
    }

    @Override
    public Object decode(Connection connection, ChannelBuffer buffer) throws DecoderException {
        // 读取头部字段
        int readable = buffer.readableBytes();
        byte[] header = new byte[Math.min(readable, HEADER_LENGTH)];
        buffer.readBytes(header);
        return decode(connection, buffer, readable, header);
    }

    private Object decode(Connection connection, ChannelBuffer buffer, int readable, byte[] header) throws DecoderException {
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // check magic
        checkMagic(header, readable);
        // checkPlayLoad
        int len = Bytes.bytes2int(header, 10);
        checkPlayLoad(connection, len);
        // 判断是否拆包
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // 将消息体封装到流
        ChannelBufferInputStream is = new ChannelBufferInputStream(buffer, len);
        return decodeBody(header, is);

    }

    private Object decodeBody(byte[] header, ChannelBufferInputStream is) {
        // 获取序列化器编号
        byte flag = header[1], prto = (byte) (flag & SERIALIZATION_MASK);
        int id = Bytes.bytes2int(header, 6);
        short cmdCode = Bytes.bytes2short(header, 2);
        if ((flag & FLAG_REQUEST) == 0) {
            ResponseCommand res = new ResponseCommand(CommandCodeEnum.toEnum(cmdCode));
            res.setHeartbeat((flag & FLAG_HEARTBEAT) != 0);
            res.setId(id);
            byte status = header[4];
            res.setStatus(ResponseStatus.toEnum(status));
            try {
                Serialization serialization = SerializationManager.getSerializationById(prto);
                ObjectInput input = serialization.deserialize(is);
                if (ResponseStatus.SUCCESS.equals(res.getStatus())) {
                    if (res.isHeartbeat()) {
                        // heartBeat不用解码，心跳也可以携带body
                    }
                    DecodeableInvocation inv = new DecodeableInvocation(res,
                            new UnsafeByteArrayInputStream(readMessageData(is)), prto);
                    res.setInvocation(inv);
                } else {
                    res.setErrorMessage(input.readUTF());
                    // 不会读取
                    DecodeableInvocation inv = new DecodeableInvocation(res,
                            new UnsafeByteArrayInputStream(readMessageData(is)), prto);
                    res.setInvocation(inv);
                }
            } catch (Throwable t) {
                logger.warn("Decode response failed: {}", t.getMessage(), t);
                res.setStatus(ResponseStatus.CODEC_EXCEPTION);
                res.setErrorMessage(ObjectUtils.toString(t));
            }
            return res;
        } else {
            RequestCommand req = new RequestCommand(CommandCodeEnum.toEnum(cmdCode));
            try {
                req.setHeartbeat((flag & FLAG_HEARTBEAT) != 0);
                req.setTwoWay((flag & FLAG_TWOWAY) != 0);
                req.setId(id);
                req.setVersion(Version.getProtocolVersion());
                if (req.isHeartbeat()) {

                }

                // 业务线程解码,先把消息体读到字节数组中
                DecodeableInvocation inv = new DecodeableInvocation(req,
                        new UnsafeByteArrayInputStream(readMessageData(is)), prto);
                req.setInvocation(inv);
                return req;
            } catch (Throwable t) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Decode request failed: " + t.getMessage(), t);
                }
                // bad request
                req.setBroken(true);
                Invocation inv = new Invocation<Throwable>();
                inv.setClassName(t.getClass().getName());
                inv.setData(t);
                req.setInvocation(inv);
            }

        }
        return DecodeResult.NEED_MORE_INPUT;
    }


    private void encodeResponse(Connection con, ResponseCommand res, ChannelBuffer buffer) throws EncoderException {
        try {
            Serialization serialization = SerializationManager.getSerialization(con.getUrl());
            byte[] header = new byte[HEADER_LENGTH];
            header[0] = MAGIC;
            header[1] = serialization.getContentTypeId();
            if (res.isHeartbeat()) {
                header[1] |= FLAG_HEARTBEAT;
            }
            Bytes.short2bytes(res.getCmdCode().getValue(), header, 2);
            header[4] = res.getStatus().value();
            // TODO
            header[5] = FLAGS_MASK;
            Bytes.int2bytes(res.getId(), header, 6);

            int savedWriteIndex = buffer.writerIndex();
            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
            ChannelBufferOutputStream bos = new ChannelBufferOutputStream(buffer);

            ObjectOutput out = serialization.serialize(bos);
            // encode response data or error message.
            if (ResponseStatus.SUCCESS.equals(res.getStatus())) {
                if (res.isHeartbeat()) {
                    encodeData(out, res.getInvocation(), null);
                } else {
                    encodeData(out, res.getInvocation(), Version.getProtocolVersion());
                }
            } else {
                out.writeUTF(res.getErrorMessage());
            }
            out.flushBuffer();
            // 关闭流
            bos.flush();
            bos.close();
            // 获取写入的字节长度
            int len = bos.writtenBytes();
            checkPlayLoad(con, len);
            Bytes.int2bytes(len, header, 10);
            //reset写指针
            buffer.writerIndex(savedWriteIndex);
            // 写消息头
            buffer.writeBytes(header);
            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + len);
        } catch (IOException e) {
            throw new DecoderException("encode response failed !");
        }
    }

    // TODO fix
    private void encodeRequst(Connection con, RequestCommand req, ChannelBuffer buffer) throws EncoderException {
        try {
            Serialization serialization = SerializationManager.getSerialization(con.getUrl());
            byte[] header = new byte[HEADER_LENGTH];
            header[0] = MAGIC;
            header[1] = (byte) (FLAG_REQUEST | serialization.getContentTypeId());
            if (req.isTwoWay()) {
                header[1] |= FLAG_TWOWAY;
            }
            if (req.isHeartbeat()) {
                header[1] |= FLAG_HEARTBEAT;
            }
            Bytes.short2bytes(req.getCmdCode().getValue(), header, 2);
            header[4] = (byte) req.getTimeout();
            // TODO CRC校验
            header[5] = FLAGS_MASK;
            Bytes.int2bytes(req.getId(), header, 6);

            // 将写指针后移头部消息头长度
            int savedWriteIndex = buffer.writerIndex();
            // 移动写指针到写body起始位置
            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
            ChannelBufferOutputStream bos = new ChannelBufferOutputStream(buffer);
            ObjectOutput out = serialization.serialize(bos);
            if (req.isHeartbeat()) {
                encodeData(out, req.getInvocation(), null);
            } else {
                encodeData(out, req.getInvocation(), Version.getProtocolVersion());
            }
            out.flushBuffer();
            // 关闭流
            bos.flush();
            bos.close();
            // 获取写入的字节长度
            int len = bos.writtenBytes();
            checkPlayLoad(con, len);
            Bytes.int2bytes(len, header, 10);
            //reset写指针
            buffer.writerIndex(savedWriteIndex);
            // 写消息头
            buffer.writeBytes(header);
            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + len);
        } catch (IOException e) {
            throw new EncoderException("encode request failed !", e);
        }
    }

    private void encodeData(ObjectOutput out, Invocation inv, String version) throws IOException {
        if (version != null) {
            out.writeUTF(version);
        }
        if (inv != null) {
            out.writeUTF(inv.getClassName());
            out.writeObject(inv.getData());
        }
    }

    private byte[] readMessageData(InputStream is) throws IOException {
        if (is.available() > 0) {
            byte[] result = new byte[is.available()];
            is.read(result);
            return result;
        }
        return new byte[]{};
    }

    @Override
    protected byte getMagicCode() {
        return MAGIC;
    }
}
