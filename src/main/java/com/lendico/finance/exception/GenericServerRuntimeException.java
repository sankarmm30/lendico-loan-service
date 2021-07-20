package com.lendico.finance.exception;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class GenericServerRuntimeException extends GenericRuntimeException {

    public GenericServerRuntimeException(String message) {
        super(message);
    }

    public GenericServerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}