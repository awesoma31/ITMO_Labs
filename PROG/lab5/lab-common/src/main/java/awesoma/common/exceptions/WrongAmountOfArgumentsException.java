package awesoma.common.exceptions;

public class WrongAmountOfArgumentsException extends RuntimeException {
    public WrongAmountOfArgumentsException(String msg) {
        super(msg);
    }

    public WrongAmountOfArgumentsException() {
        super("Wrong amount of args given to command");
    }
}
