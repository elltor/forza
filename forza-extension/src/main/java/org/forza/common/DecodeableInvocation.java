package org.forza.common;

import io.netty.handler.codec.DecoderException;
import org.forza.common.command.AbstractCommand;
import org.forza.common.command.ResponseCommand;
import org.forza.common.enums.ResponseStatus;
import org.forza.serialization.ObjectInput;
import org.forza.serialization.Serialization;
import org.forza.serialization.SerializationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class DecodeableInvocation extends Invocation implements Decdeable {
    private static final Logger logger = LoggerFactory.getLogger(DecodeableInvocation.class);

    private byte serializationType;
    private InputStream inputStream;
    private AbstractCommand command;
    private static final int DESERIALIZE_NO = 1;
    private static final int DESERIALIZE_KEY = 2;
    private static final int DESERIALIZE_DATA = 3;

    private volatile int state = DESERIALIZE_NO;
    private volatile ObjectInput objectInput;

    private static final AtomicIntegerFieldUpdater<DecodeableInvocation> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(DecodeableInvocation.class, "state");

    public DecodeableInvocation(AbstractCommand command, InputStream is, byte id) {
        Objects.requireNonNull(is, "inputStream == null");
        Objects.requireNonNull(command, "Command == null");
        this.command = command;
        this.inputStream = is;
        this.serializationType = id;
    }

    @Override
    public void decodeClassName() throws DecoderException {
        try {
            if (state == DESERIALIZE_NO && inputStream.available() > 0) {
                // Response
                if (command instanceof ResponseCommand) {
                    ResponseCommand response = (ResponseCommand) command;
                    if (!ResponseStatus.SUCCESS.equals(response.getStatus())) {
                        return;
                    }
                }
                if (STATE_UPDATER.compareAndSet(this, DESERIALIZE_NO, DESERIALIZE_KEY)) {
                    Serialization serialization = SerializationManager.getSerializationById(serializationType);
                    this.objectInput = serialization.deserialize(inputStream);
                    String version = objectInput.readUTF();
                    command.setVersion(version);
                    String className = objectInput.readUTF();
                    this.setClassName(className);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Decode ClassName {}", getClassName());
                    }
                }
            }
        } catch (IOException e) {
            throw new DecoderException("Decode invocation ClassName failed", e);
        }
    }

    @Override
    public void decodeData() throws DecoderException {
        try {
            if (state == DESERIALIZE_KEY) {
                if (STATE_UPDATER.compareAndSet(this, DESERIALIZE_KEY, DESERIALIZE_DATA)) {
                    Class<?> clazz = Class.forName(this.getClassName(), false, Thread.currentThread().getContextClassLoader());
                    this.setData(objectInput.readObject(clazz));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Decode data {}", getData().toString());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new DecoderException("Decode invocation data failed", e);
        }

    }
}
