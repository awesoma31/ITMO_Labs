package awesoma.common.exceptions;

/**
 * Exception if command got wrong amount of args
 */
public class WrongAmountOfArgumentsException extends RuntimeException {
    public WrongAmountOfArgumentsException(String msg) {
        super(msg);
    }

    public WrongAmountOfArgumentsException() {
        super("Wrong amount of args given to command");
    }
}
