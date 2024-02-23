package awesoma.common.exceptions;

public class InfiniteScriptCallLoopException extends CommandExecutingException{
    public InfiniteScriptCallLoopException() {
        super("Infinite cycle of calling script occurs");
    }

    public InfiniteScriptCallLoopException(String msg) {
        super("Infinite cycle of calling script occurs: " + msg);
    }
}
