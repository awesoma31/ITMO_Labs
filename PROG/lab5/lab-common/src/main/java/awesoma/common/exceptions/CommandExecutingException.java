package awesoma.common.exceptions;

/**
 * General exception if fail while executing command
 */
public class CommandExecutingException extends Exception {
    public CommandExecutingException() {
        super("Fail while executing command");
    }

    public CommandExecutingException(String msg) {
        super(msg);
    }
}
