package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;

import java.util.ArrayList;

/**
 * interface represents command execution cycle
 */
public interface ExecutAble {
    /**
     * @param args that command accepts
     * @throws CommandExecutingException if execution failed
     */
    void execute(ArrayList<String> args) throws CommandExecutingException;

}
