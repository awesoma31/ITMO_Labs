package org.awesoma.common.exceptions;

/**
 * Exception if args validation failed
 */
public class ValidationException extends Exception {
    public ValidationException(String msg) {
        super(msg);
    }
}
