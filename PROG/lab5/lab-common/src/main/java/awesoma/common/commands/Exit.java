package awesoma.common.commands;

import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.managers.CommandManager;

import java.util.ArrayList;

public class Exit extends Command {
    public static final int argAmount = 0;

    public Exit() {
        super(
                "exit",
                "This command exits from the program without saving collection to file"
//                commandManager
        );
    }

    @Override
    public void execute(ArrayList<String> args, CommandManager commandManager) {
        if (args.size() == argAmount) {
            System.out.print("[INFO]: Terminating process");
            System.exit(0);
        } else {
            throw new WrongAmountOfArgumentsException();
        }

    }
}