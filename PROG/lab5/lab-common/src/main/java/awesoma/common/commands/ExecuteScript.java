package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.InfiniteScriptCallLoopException;
import awesoma.common.exceptions.UnrecognisedCommandException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * this command executes script from the file string by string
 */
public class ExecuteScript extends Command {
    public static final int argAmount = 1;
    private final Vector<Movie> collection;
    public HashSet<String> used_paths = new HashSet<>();
    private HashMap<String, Command> commands;
    public FileInputStream fis;
    private BufferedReader reader;

    public ExecuteScript() {
        super("execute_script", "This command executes script from given file");
        this.collection = null;
        this.commands = null;

    }

    public ExecuteScript(Vector<Movie> collection, HashMap<String, Command> commands) {
        super("execute_script", "This command executes script from given file");
        this.collection = collection;
        this.commands = commands;
    }

    public void setRegisteredCommands(HashMap<String, Command> commands) {
        this.commands = commands;
    }

    /**
     * @param comName command to find
     * @return Command class by its name
     * @throws UnrecognisedCommandException if command not found in registered
     */
    public Command getCommand(String comName) throws UnrecognisedCommandException {
        try {
            return commands.get(comName);
        } catch (NullPointerException e) {
            throw new UnrecognisedCommandException();
        }

    }

    // TODO file not found
    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {

        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            String path = args.get(0);

            try (
                    FileInputStream fis = new FileInputStream(path);
                    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr)
            ) {
//                fis = new FileInputStream(path);


                // execute_script loop_script1.txt
                // execute_script simple_script.txt
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] input_data_ = line.split(" ");
                        String commandName_ = input_data_[0];
                        Command command_ = getCommand(commandName_);
                        ArrayList<String> args_ = new ArrayList<>(Arrays.asList(input_data_).subList(1, input_data_.length));

                        if (commandName_.equals("execute_script")) {
                            if (used_paths.contains(args_.get(0))) {
                                throw new InfiniteScriptCallLoopException();
                            } else {
                                used_paths.add(args_.get(0));
                            }
                        }

                        command_.execute(args_);

                    } catch (NullPointerException e) {
                        System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
                    } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                        System.out.println(e.getMessage());
                    } catch (UnrecognisedCommandException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
