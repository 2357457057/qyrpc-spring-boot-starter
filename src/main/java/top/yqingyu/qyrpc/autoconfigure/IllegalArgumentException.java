package top.yqingyu.qyrpc.autoconfigure;

import top.yqingyu.common.exception.QyRuntimeException;

public class IllegalArgumentException extends QyRuntimeException {
    public IllegalArgumentException() {
    }

    public IllegalArgumentException(String message, Object... o) {
        super(message, o);
    }

    public IllegalArgumentException(Throwable cause, String message, Object... o) {
        super(cause, message, o);
    }

    public IllegalArgumentException(Throwable cause) {
        super(cause);
    }

    public IllegalArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... o) {
        super(message, cause, enableSuppression, writableStackTrace, o);
    }
}
