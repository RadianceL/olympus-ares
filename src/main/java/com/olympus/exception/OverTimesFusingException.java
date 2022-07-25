package com.olympus.exception;

/**
 * 多次请求熔断异常
 * since 7/25/22
 *
 * @author eddie
 */
public class OverTimesFusingException extends RuntimeException {

    public OverTimesFusingException() {
    }

    public OverTimesFusingException(String message) {
        super(message);
    }

    public OverTimesFusingException(String message, Throwable cause) {
        super(message, cause);
    }
}
