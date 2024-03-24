package awesoma.common.exceptions.commands;

import awesoma.common.exceptions.exceptions.CommandExecutingException;
import awesoma.common.exceptions.exceptions.InfiniteScriptCallLoopException;
import awesoma.common.exceptions.exceptions.UnrecognisedCommandException;
import awesoma.common.exceptions.exceptions.WrongAmountOfArgumentsException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


/**
 * this command executes script from the file string by string
 */
public class ExecuteScript extends Command {
    protected static final int argAmount = 1;
    protected HashSet<String> used_paths = new HashSet<>();
    private HashMap<String, Command> commands;

    public ExecuteScript() {
        super("execute_script", "This command executes script from given file");
        this.commands = null;
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

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {

        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            String path = args.get(0);

            try {
                BufferedReader fis = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                String line;
                setReader(fis);
                while ((line = reader.readLine()) != null) {
                    try {
                        if (!line.isEmpty()) {
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

                            command_.setReader(fis);
                            command_.execute(args_);
                            command_.setReader(defaultReader);
                        }
                    } catch (NullPointerException e) {
                        System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
                    } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                        System.out.println(e.getMessage());
                    } catch (UnrecognisedCommandException e) {
                        throw new RuntimeException(e);
                    }
                }
                setReader(defaultReader);

            } catch (FileNotFoundException e) {
                throw new CommandExecutingException("Script with such name not found");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
