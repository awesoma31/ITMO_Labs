package awesoma.common.exceptions;

public class ArgParsingException extends CommandExecutingException {
    public ArgParsingException() {
        super("[FAIL]: Error while parsing an argument");
    }

    public ArgParsingException(String message) {
        super(message);
    }
}
