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

/**
 * Class represents interactive console while running
 */
public class Console {
    private final BufferedReader reader;
    private HashMap<String, Command> registeredCommands = new HashMap<>();


    public Console(
            Command[] commandsToReg,
            BufferedReader reader
    ) {
        registerCommands(new ArrayList<>(Arrays.asList(commandsToReg)));
        this.reader = reader;
    }

    /**
     * @param comName name of command you want to find
     * @return Command from registered commands
     * @throws UnrecognisedCommandException if command is not registered or doesn't exist
     */
    public Command getCommand(String comName) throws UnrecognisedCommandException {
        try {
            return registeredCommands.get(comName);
        } catch (NullPointerException e) {
            throw new UnrecognisedCommandException();
        }

    }

    /**
     * @return registered commands
     */
    public HashMap<String, Command> getRegisteredCommands() {
        return registeredCommands;
    }


    /**
     * registers given commands
     *
     * @param commands to register
     */
    public void registerCommands(ArrayList<Command> commands) {
        for (Command c : commands) {
            registeredCommands.put(c.getName(), c);
        }
    }

    /**
     * runs interactive mode of console
     */
    public void interactiveMode() {
        System.out.println("[INFO]: Program started");

        String input;
        while (true) {
            System.out.print("-> ");
            try {
                input = reader.readLine();
                if (input == null) {
                    System.exit(0);
                }
                input = input.trim();
                if (!input.isEmpty()) {
                    String[] input_data = input.split(" ");
                    String commandName = input_data[0];
                    Command command = getCommand(commandName);
                    ArrayList<String> args = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                    command.execute(args);
                }

            } catch (NullPointerException e) {
//                System.err.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
                System.err.println(e);
            } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                System.err.println(e.getMessage());
            } catch (UnrecognisedCommandException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
