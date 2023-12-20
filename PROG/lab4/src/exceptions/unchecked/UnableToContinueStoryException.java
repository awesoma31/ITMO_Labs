package exceptions.unchecked;

public class UnableToContinueStoryException extends RuntimeException{
    public UnableToContinueStoryException(String msg) {
        super(msg);
    }
}
