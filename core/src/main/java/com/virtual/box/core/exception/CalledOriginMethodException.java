package com.virtual.box.core.exception;

public class CalledOriginMethodException extends Exception{

    public CalledOriginMethodException(String message) {
        super(message);
    }

    public CalledOriginMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public CalledOriginMethodException(Throwable cause) {
        super(cause);
    }

}
