package awesoma.common.commands;

import awesoma.common.exceptions.WrongAmountOfArgumentsException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this commands shows name and description of registered commands
 */
public class Help extends Command {
    public static final int argAmount = 0;
    private HashMap<String, Command> commands = null;

    public Help() {
        super(
                "help",
                "This command shows info about available commands"
        );
    }

    @Override
    public void execute(ArrayList<String> args) {
        if (args.size() == Help.argAmount & commands != null) {
            commands.forEach((commandName, command) ->
                    System.out.println(
                            "<" + commandName +
                                    ">; " + command.getDescription()
                    ));
        } else {
            throw new WrongAmountOfArgumentsException();
        }

    }

    public void setRegisteredCommands(HashMap<String, Command> commands) {
        this.commands = commands;
    }
}
