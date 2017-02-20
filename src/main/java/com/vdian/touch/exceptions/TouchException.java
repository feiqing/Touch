package com.vdian.touch.exceptions;

/**
 * @author jifang
 * @since 16/10/20 下午11:53.
 */
public class TouchException extends RuntimeException {

    public TouchException() {
        super();
    }

    public TouchException(String message) {
        super(message);
    }

    public TouchException(String message, Throwable cause) {
        super(message, cause);
    }

    public TouchException(Throwable cause) {
        super(cause);
    }

    protected TouchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
