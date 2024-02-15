package awesoma.common.exceptions;

public class UnrecognisedCommandException extends NullPointerException{
    public UnrecognisedCommandException() {
        super("This command is not registered or doesnt exist");
        System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
    }
}
