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

public class ExecuteScript extends Command {
    public static final int argAmount = 1;
    private final Vector<Movie> collection;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private HashMap<String, Command> commands;

    public ExecuteScript() {
        super("execute_script", "This command executes script from given file");
        this.collection = new Vector<Movie>();
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

    public Command getCommand(String comName) throws UnrecognisedCommandException {
        try {
            return commands.get(comName);
        } catch (NullPointerException e) {
            throw new UnrecognisedCommandException();
        }

    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        String path = args.get(0);

        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            try (FileInputStream fis = new FileInputStream(path);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)) {
                HashSet<String> used_paths = new HashSet<>();

                // execute_script loop_script1.txt
                // execute_script simple_script.txt
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] input_data = line.split(" ");
                        String commandName_ = input_data[0];
                        Command command_ = getCommand(commandName_);
                        ArrayList<String> args_ = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                        System.out.println("com_ name: " + commandName_);
                        System.out.println("com_ class: " + command_.getClass());
                        System.out.println("used_paths " + used_paths);
                        System.out.println("args_ " + used_paths);
                        System.out.println();
                        if (commandName_.equals("execute_script") ) {
                            if (used_paths.contains(args_.get(0))) {
                                throw new InfiniteScriptCallLoopException();
                            }
                            used_paths.add(args_.get(0));
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
