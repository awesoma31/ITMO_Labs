package awesoma.common.exceptions;

public class ConvertationException extends RuntimeException {
    public ConvertationException(String msg) {
        super(msg);
    }

    public ConvertationException() {
        super("Fail to convert an object");
    }
}
