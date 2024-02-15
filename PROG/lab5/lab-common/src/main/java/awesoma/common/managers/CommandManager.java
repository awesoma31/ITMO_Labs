package awesoma.common.managers;

import awesoma.common.commands.Command;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandManager {
    private static final int COMMAND_HISTORY_MAX_SIZE = 13;
    private ArrayList<String> history = new ArrayList<>();
    private HashMap<String, Command> registeredCommands = new HashMap<>();

    public CommandManager() {
    }

    public void addToHistory(Command command) {
        if (history.size() < COMMAND_HISTORY_MAX_SIZE) {
            history.add(command.getName());
        } else {
            history.remove(0);
            history.add(command.getName());
        }
    }

    public void register(Command command) {
        registeredCommands.put(command.getName(), command);
    }

    public void registerCommands(ArrayList<Command> commands) {
        for (Command c : commands) {
            registeredCommands.put(c.getName(), c);
        }
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setRegisteredCommands(HashMap<String, Command> registeredCommands) {
        this.registeredCommands = registeredCommands;
    }

    public HashMap<String, Command> getRegisteredCommands() {
        return registeredCommands;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }
}
