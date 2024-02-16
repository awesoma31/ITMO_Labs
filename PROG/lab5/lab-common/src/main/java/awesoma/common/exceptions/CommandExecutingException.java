package awesoma.common.exceptions;

public class CommandExecutingException extends Exception {
    public CommandExecutingException() {
        super("Fail while executing command");
    }

    public CommandExecutingException(String msg) {
        super(msg);
    }
}
