package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.managers.CommandManager;

import java.util.ArrayList;

public interface ExecutAble {
    void execute(ArrayList<String> args, CommandManager commandManager) throws CommandExecutingException;

}
