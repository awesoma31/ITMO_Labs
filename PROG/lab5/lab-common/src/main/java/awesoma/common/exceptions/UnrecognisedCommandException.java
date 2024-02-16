package awesoma.common.exceptions;

public class UnrecognisedCommandException extends Exception {
    public UnrecognisedCommandException() {
        super("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
//        System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
    }
}
