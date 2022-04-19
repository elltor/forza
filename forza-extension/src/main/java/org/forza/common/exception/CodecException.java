package org.forza.common.exception;

import java.io.IOException;

/**
 * @Author:  
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
public class CodecException extends IOException {
    public CodecException() {
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

}
