package org.awesoma.common.commands;

import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;

import java.util.ArrayList;

/**
 * this command closes the program without saving the collection to file
 */
public class Exit extends AbstractCommand {
    public Exit() {
        super(
                "exit",
                "This command exits from the program without saving collection to file"
        );
    }

    public Exit(String name, String description) {
        super(name, description);
    }

    @Override
    public void execute(ArrayList<String> args) {
        if (args.size() == argAmount) {
            System.out.print("[INFO]: Terminating process");
            System.exit(0);
        } else {
            throw new WrongAmountOfArgumentsException();
        }

    }
}
