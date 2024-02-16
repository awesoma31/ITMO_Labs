package awesoma.common.exceptions;

public class CommandsInfoNotFoundException extends CommandExecutingException {
    public CommandsInfoNotFoundException() {
        super("Info about commands not found");
    }
}
