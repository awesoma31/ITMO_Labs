package awesoma.common.exceptions;

import awesoma.common.commands.Command;

public class WrongAmountOfArgumentsException extends RuntimeException{
    public WrongAmountOfArgumentsException() {
        super("Wrong amount of args given");
    }
}
