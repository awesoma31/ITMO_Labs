package awesoma.managers;

import awesoma.common.commands.*;
import awesoma.common.exceptions.UnrecognisedCommandException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class Console {
    private final HashMap<String, Command> registeredCommands;
    private final CommandManager commandManager;
    private final TreeSet<Movie> collection;
    private final Date initDate = new Date();

    public Console(HashMap<String, Command> registeredCommands, CommandManager commandManager, TreeSet<Movie> collection) {
        this.registeredCommands = registeredCommands;
        this.commandManager = commandManager;
        this.collection = collection;
    }

    public Command getCommand(String comName) throws UnrecognisedCommandException{
        try {
            return registeredCommands.get(comName);
        } catch (NullPointerException e) {
            throw new UnrecognisedCommandException();
        }

    }

    public void executeCommand(Command command_, String[] args) throws WrongAmountOfArgumentsException {
        if (command_.getClass() == Exit.class) {
            Exit command = (Exit) command_;
            command.execute();
        } else if (command_.getClass() == Help.class) {
            Help command = (Help) command_;
            // TODO try if file not found
            command.execute(
                    "C:\\Users\\gwert\\Documents\\ITMO_Labs\\PROG\\lab5_archetype\\lab\\lab-common\\src\\main\\java\\awesoma\\common\\commands\\command_info.txt",
                    commandManager
            );
        } else if (command_.getClass() == Info.class) {
            Info command = (Info) command_;
            command.execute(collection, commandManager);
        } else if (command_.getClass() == Show.class) {
            Show command = (Show) command_;
            command.execute(collection, commandManager);
        } else if (command_.getClass() == Clear.class) {
            Clear command = (Clear) command_;
            command.execute(collection, commandManager);
        } else if (command_.getClass() == History.class) {
            History command = (History) command_;
            command.execute(commandManager);
        } else if (command_.getClass() == CountLessThanOscarsCount.class) {
            // TODO args
            CountLessThanOscarsCount command = (CountLessThanOscarsCount) command_;
            if (args.length == 1) {
                System.out.println(command.execute(collection, Long.valueOf(args[0]), commandManager));;
            } else {
                throw new WrongAmountOfArgumentsException();
            }
        }
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);


        while (true) {
            String input = scanner.nextLine().trim();
            String[] input_data = input.split(" ");
            String[] args = new String[input_data.length - 1];

            System.arraycopy(input_data, 1, args, 0, input_data.length - 1);

            try {
                Command command = getCommand(input_data[0]);

                executeCommand(command, args);

            } catch (NullPointerException e) {
                System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
                continue;
            } catch (WrongAmountOfArgumentsException e) {
                System.out.println(e.getMessage());
                continue;
            }


            if (input.equals("q")) {
                scanner.close();
                System.exit(0);
            }
        }
    }
}
