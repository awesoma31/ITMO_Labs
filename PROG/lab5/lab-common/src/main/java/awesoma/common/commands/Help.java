package awesoma.common.commands;

import awesoma.common.managers.CommandManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Help extends Command {

    public Help() {
        super(
                "help",
                "This command shows info about available commands"
//                commandManager
        );
    }

    public void execute(String path, CommandManager commandManager) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Info about commands not found");
            System.exit(1);
        }
        commandManager.addToHistory(this);
    }

    // "C:\\Users\\gwert\\Documents\\ITMO_Labs\\PROG\\lab5_archetype\\lab\\lab-common\\src\\main\\java\\awesoma\\common\\commands\\command_info.txt"


}
