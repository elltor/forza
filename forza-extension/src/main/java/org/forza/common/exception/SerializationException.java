package org.forza.common.exception;

/**
 * @Author:  
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
public class SerializationException extends CodecException {
    public SerializationException() {
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
