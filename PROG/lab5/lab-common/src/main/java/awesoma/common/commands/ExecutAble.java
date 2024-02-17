package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;

import java.util.ArrayList;

public interface ExecutAble {
    void execute(ArrayList<String> args) throws CommandExecutingException;

}
