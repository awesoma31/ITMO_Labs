package awesoma.common.commands;

import awesoma.common.exceptions.CommandsInfoNotFoundException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.managers.CommandManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Help extends Command {
    public static final int argAmount = 0;
    private final String file_path;

    public Help(String file_path) {
        super(
                "help",
                "This command shows info about available commands"
//                commandManager
        );
        this.file_path = file_path;
    }

    @Override
    public void execute(ArrayList<String> args, CommandManager commandManager) throws CommandsInfoNotFoundException {
        if (args.size() == Help.argAmount) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file_path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                throw new CommandsInfoNotFoundException();
            }
            commandManager.addToHistory(this);
        } else {
            throw new WrongAmountOfArgumentsException();
        }

    }

    public String getFile_path() {
        return file_path;
    }

    // "C:\\Users\\gwert\\Documents\\ITMO_Labs\\PROG\\lab5_archetype\\lab\\lab-common\\src\\main\\java\\awesoma\\common\\commands\\command_info.txt"


}
