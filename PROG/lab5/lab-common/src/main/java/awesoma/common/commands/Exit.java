package awesoma.common.commands;

import awesoma.common.managers.CommandManager;

public class Exit extends Command{
    public Exit() {
        super(
                "exit",
                "This command exits from the program without saving collection to file"
//                commandManager
        );
    }

    public void execute() {
        System.out.println("Exiting the program");
        System.exit(0);
    }
}
