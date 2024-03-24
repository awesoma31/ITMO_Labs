package org.awesoma;

import org.awesoma.commands.AbstractClientCommand;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.UnrecognisedCommandException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class represents interactive console while running
 */
public class Console {
    private final BufferedReader reader;
    private final HashMap<String, AbstractClientCommand> registeredCommands = new HashMap<>();


    public Console(
            AbstractClientCommand[] commandsToReg,
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
    public AbstractClientCommand getCommand(String comName) throws UnrecognisedCommandException {
        try {
            return registeredCommands.get(comName);
        } catch (NullPointerException e) {
            throw new UnrecognisedCommandException();
        }

    }

    /**
     * @return registered commands
     */
    public HashMap<String, AbstractClientCommand> getRegisteredCommands() {
        return registeredCommands;
    }


    /**
     * registers given commands
     *
     * @param abstractCommands to register
     */
    public void registerCommands(ArrayList<AbstractClientCommand> abstractCommands) {
        for (AbstractClientCommand c : abstractCommands) {
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
            System.out.print("");
            try {
                input = reader.readLine();

                if (input == null) {
                    System.exit(0);
                }

                input = input.trim();
                if (!input.isEmpty()) {
                    String[] input_data = input.split(" ");
                    String commandName = input_data[0];
                    AbstractClientCommand abstractCommand = getCommand(commandName);
                    ArrayList<String> args = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                    abstractCommand.execute(args);
                }

            } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                System.err.println(e.getMessage());
            } catch (UnrecognisedCommandException | IOException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                System.err.println("[FAIL]: This command is not recognised");
            }
        }
    }
}
