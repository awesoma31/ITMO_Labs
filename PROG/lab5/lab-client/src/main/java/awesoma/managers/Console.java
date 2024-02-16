package awesoma.managers;

import awesoma.common.commands.Command;
import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.UnrecognisedCommandException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class Console {
    private final HashMap<String, Command> registeredCommands;
    private final CommandManager commandManager;
    private final Vector<Movie> collection;
    private ArrayList<String> history = new ArrayList<>();
    private BufferedReader reader;


    public Console(
            HashMap<String, Command> registeredCommands,
            CommandManager commandManager,
            BufferedReader reader,
            Vector<Movie> collection
    ) {
        this.registeredCommands = registeredCommands;
        this.commandManager = commandManager;
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

    public void executeCommand(Command command_, String[] args, BufferedReader reader) {
//        if (command_.getClass() == Exit.class) {
//            Exit command = (Exit) command_;
//            command.execute();
//        } else if (command_.getClass() == Help.class) {
//            Help command = (Help) command_;
//            // TODO try if file not found
//            command.execute(
//                    "C:\\Users\\gwert\\Documents\\ITMO_Labs\\PROG\\lab5_archetype\\lab\\lab-common\\src\\main\\java\\awesoma\\common\\commands\\command_info.txt",
//                    commandManager
//            );
//        } else if (command_.getClass() == Info.class) {
//            Info command = (Info) command_;
//            command.execute(collection, commandManager);
//        } else if (command_.getClass() == Show.class) {
//            Show command = (Show) command_;
//            command.execute(collection, commandManager);
//        } else if (command_.getClass() == Clear.class) {
//            Clear command = (Clear) command_;
//            command.execute(collection, commandManager);
//        } else if (command_.getClass() == History.class) {
//            History command = (History) command_;
//            command.execute(commandManager);
//        } else if (command_.getClass() == CountLessThanOscarsCount.class) {
//            // TODO args
//            CountLessThanOscarsCount command = (CountLessThanOscarsCount) command_;
//            if (args.length == CountLessThanOscarsCount.argAmount) {
//                try {
//                    System.out.println(command.execute(collection, Long.valueOf(args[0]), commandManager));
//                    ;
//                } catch (NumberFormatException e) {
//                    throw new ArgParsingException("Fail while parsing an argument. Its type must be long");
//                }
//            } else {
//                throw new WrongAmountOfArgumentsException("Wrong amount of args given, only 1 required");
//            }
//        } else if (command_.getClass() == CountByOperator.class) {
//            // TODO count by operator
//            CountByOperator command = (CountByOperator) command_;
////            command.execute();
//        } else if (command_.getClass() == FilterStartsWithName.class) {
//            FilterStartsWithName command = (FilterStartsWithName) command_;
//            if (args.length == FilterStartsWithName.argAmount) {
//                try {
//                    command.execute(collection, args[0], commandManager);
//                } catch (NumberFormatException e) {
//                    throw new ArgParsingException("Fail while parsing an argument. Its type must be String");
//                }
//            } else {
//                throw new WrongAmountOfArgumentsException("Wrong amount of args given, 1 required");
//            }
//        } else if (command_.getClass() == Add.class) {
//            double id;
//            String name;
//            Long oscarsCount = null, totalBoxOffice, x;
//            Float usaBoxOffice, y = null;
//            Coordinates coordinates;
//            Date creationDate;
//            MovieGenre genre;
//            Person operator;
//
//            Add command = (Add) command_;
//
//            // TODO auto generate
//            // id
//            System.out.print("input (double, notNull) id: ");
//
//            try {
//                id = Double.parseDouble(reader.readLine());
//                System.out.println("id: " + id);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            // TODO name почему пропускает
//            System.out.print("input (String, notNull) name: ");
//            try {
//                name = reader.readLine();
//                System.out.println("name: " + name);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
////            name = scanner.nextLine();
//
//            // Coordinates
//            // x
//            System.out.print("input coordinate (Long) x: ");
//            String tmp = null;
//            try {
//                tmp = reader.readLine();
//                if (Objects.equals(tmp, "")) {
//                    x = null;
//                } else {
//                    try {
//                        x = Long.valueOf(tmp);
//                    } catch (NumberFormatException e) {
//                        throw new ArgParsingException("<x> must be convertable to Long");
//                    }
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            // y
//            do {
//                System.out.print("input coordinate (Float) y: ");
//                try {
//                    y = Float.valueOf(reader.readLine());
//                    System.out.println("y: " + y);
//                } catch (InputMismatchException e) {
//                    System.out.println("Use comma to separate parts of float");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                if (y > 117 | y == null ) {
//                    System.out.println("<y> cant be null and above 117");
//                }
////                System.out.println(y);
//            } while (y > 117 | y == null);
//
//            // Date
//            creationDate = new Date();
//
//            // oscarsCount
//            do {
//                System.out.print("input (Long, >0) oscarsCount: ");
//                try {
//                    tmp = reader.readLine();
//                    if (tmp.isEmpty()) {
//                        oscarsCount = null;
//                    } else {
//                        oscarsCount = Long.valueOf(tmp);
//                    }
//                } catch (IOException e) {
//                    throw new ArgParsingException("<oscarsCount> must be convertable to Long");
//                }
//            } while (oscarsCount <= 0);
//            System.out.println("oscarsCount: " + oscarsCount);
//
//            //totalBoxOffice
//            totalBoxOffice = null;
//            do {
//                System.out.print("input (Long, notNull, >0) totalBoxOffice: ");
//                try {
//                    tmp = reader.readLine();
//                    try {
//                        totalBoxOffice = Long.valueOf(tmp);
//                    } catch (NumberFormatException e) {
//                        totalBoxOffice = (long) -1;
//                    }
//                } catch (IOException e) {
//                    throw new ArgParsingException("<totalBoxOffice> must be convertable to Long");
//                }
//            } while (totalBoxOffice <= 0 | totalBoxOffice == null);
//            System.out.println("totalBoxOffice: " + totalBoxOffice);
//
//            //usaBoxOffice
//            usaBoxOffice = null;
//            do {
//                System.out.print("input (Long, notNull, >0) usaBoxOffice: ");
//                try {
//                    tmp = reader.readLine();
//                    try {
//                        usaBoxOffice = Float.valueOf(tmp);
//                    } catch (NumberFormatException e) {
//                        usaBoxOffice = (float) -1;
//                    }
//                } catch (IOException e) {
//                    throw new ArgParsingException("<totalBoxOffice> must be convertable to Long");
//                }
//            } while (usaBoxOffice <= 0 | usaBoxOffice == null);
//            System.out.println("usaBoxOffice: " + usaBoxOffice);
//
//            // genre Поле может быть null
//            genre = null;
//            do {
//                System.out.println("Available genres: " + Arrays.toString(MovieGenre.values()));
//                System.out.print("input genre: ");
//                try {
//                    tmp = reader.readLine();
//                    tmp = tmp.toLowerCase();
//                    switch (tmp) {
//                        case "comedy":
//                            genre = MovieGenre.COMEDY;
//                            break;
//                        case "horror":
//                            genre = MovieGenre.HORROR;
//                            break;
//                        case "musical":
//                            genre = MovieGenre.MUSICAL;
//                            break;
//                        default:
//                            System.out.println("[FAIL]: Genre unrecognised, try again");
//                            genre = null;
//                            break;
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            } while (genre == null);
//        }
    }

    public void interactiveMode() {
        System.out.println("[INFO]: Program started");

        String input;
        while (true) {
            System.out.print("-> ");
            try {
                input = reader.readLine().trim();
                if (input.equals("q")) {
                    System.exit(0);
                }
                String[] input_data = input.split(" ");
                String commandName = input_data[0];
                Command command = getCommand(commandName);
                ArrayList<String> args = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

//                System.out.println("[INFO]: input: " + input);
//                System.out.println("[INFO]: input_data: " + Arrays.toString(input_data));
//                System.out.println("[INFO]: args: " + args);
//                System.out.println("[INFO]: command: " + command.getName());

//                System.out.println();
//                System.out.println("[INFO]: command executing cycle started");
//                System.out.println();

                command.execute(args, commandManager);
                history.add(command.getName());

//                System.out.println();
//                System.out.println("[INFO]: command executing cycle ended");

            } catch (NullPointerException e) {
//                throw new UnrecognisedCommandException();
                System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
            } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                System.out.println(e.getMessage());
            } catch (UnrecognisedCommandException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Vector<Movie> getCollection() {
        return collection;
    }
}
