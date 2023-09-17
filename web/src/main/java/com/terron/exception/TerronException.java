package com.terron.exception;

public class TerronException extends Exception{
    public TerronException() {
        super();
    }

    public TerronException(String message) {
        super(message);
    }

    public TerronException(String message, Throwable cause) {
        super(message, cause);
    }

    public TerronException(Throwable cause) {
        super(cause);
    }

    protected TerronException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
