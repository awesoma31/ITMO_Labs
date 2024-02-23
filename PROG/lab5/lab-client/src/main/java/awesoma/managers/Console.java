package awesoma.managers;

import awesoma.common.commands.Command;
import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.UnrecognisedCommandException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class Console {
    private final Vector<Movie> collection;
    private final BufferedReader reader;
    private HashMap<String, Command> registeredCommands = new HashMap<>();


    public Console(
            Command[] commandsToReg,
            BufferedReader reader,
            Vector<Movie> collection
    ) {
        registerCommands(new ArrayList<>(Arrays.asList(commandsToReg)));
        this.collection = collection;
        this.reader = reader;
    }

    public Console(
            HashMap<String, Command> registeredCommands,
            BufferedReader reader,
            Vector<Movie> collection
    ) {
        this.registeredCommands = registeredCommands;
        this.collection = collection;
        this.reader = reader;
    }

    public Command getCommand(String comName) throws UnrecognisedCommandException {
        try {
            return registeredCommands.get(comName);
        } catch (NullPointerException e) {
            throw new UnrecognisedCommandException();
        }

    }

    public HashMap<String, Command> getRegisteredCommands() {
        return registeredCommands;
    }

    public void setRegisteredCommands(HashMap<String, Command> registeredCommands) {
        this.registeredCommands = registeredCommands;
    }

    public void registerCommands(ArrayList<Command> commands) {
        for (Command c : commands) {
            registeredCommands.put(c.getName(), c);
        }
    }

    public void interactiveMode() {
        System.out.println("[INFO]: Program started");

        String input;
        while (true) {
            System.out.print("-> ");
            try {
                input = reader.readLine().trim();

                String[] input_data = input.split(" ");
                String commandName = input_data[0];
                Command command = getCommand(commandName);
                ArrayList<String> args = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                command.execute(args);

            } catch (NullPointerException e) {
                System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
            } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                System.out.println(e.getMessage());
            } catch (UnrecognisedCommandException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Vector<Movie> getCollection() {
        return collection;
    }
}
