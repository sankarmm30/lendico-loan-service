package com.lendico.finance.exception;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class GenericClientRuntimeException extends GenericRuntimeException {

    public GenericClientRuntimeException(String message) {
        super(message);
    }

    public GenericClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}