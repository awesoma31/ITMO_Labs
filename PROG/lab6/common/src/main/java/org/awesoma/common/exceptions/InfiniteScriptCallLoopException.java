package org.awesoma.common.exceptions;

/**
 * Exception if infinite script call loop occurs
 */
public class InfiniteScriptCallLoopException extends CommandExecutingException {
    public InfiniteScriptCallLoopException() {
        super("Infinite cycle of calling script occurs, interrupting command execution, resuming program");
    }
}
