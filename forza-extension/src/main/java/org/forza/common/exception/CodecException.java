package org.forza.common.exception;

import java.io.IOException;


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
