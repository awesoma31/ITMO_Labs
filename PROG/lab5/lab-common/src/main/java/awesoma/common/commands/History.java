package awesoma.common.commands;


import awesoma.common.managers.CommandManager;

import java.util.ArrayList;

public class History extends Command {
    public History() {
        super(
                "history",
                "This command shows last used 13 commands' names"
//                commandManager
        );
    }

    // TODO где должна храниться история команд
    public ArrayList<String> execute(CommandManager commandManager) {
        return commandManager.getHistory();
    }
}
