package com.lendico.finance.exception;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class GenericRuntimeException extends RuntimeException {

    public GenericRuntimeException() {
    }

    public GenericRuntimeException(String message) {
        super(message);
    }

    public GenericRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}