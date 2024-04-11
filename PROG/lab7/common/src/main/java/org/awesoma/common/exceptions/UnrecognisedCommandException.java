package org.awesoma.common.exceptions;

/**
 * Exception if command  not found in registered commands
 */
public class UnrecognisedCommandException extends Exception {
    public UnrecognisedCommandException() {
        super("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
    }
}
