package awesoma.client;

import awesoma.common.commands.*;
import awesoma.common.managers.CommandManager;
import awesoma.common.models.*;
import awesoma.common.util.UniqueIdGenerator;
import awesoma.managers.Console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;


public final class Client {
    public static Date initDate = new Date();
    public static String HELP_FILE_PATH =
            "C:\\Users\\gwert\\Documents\\ITMO_Labs" +
                    "\\PROG\\lab5_archetype\\lab\\" +
                    "lab-common\\src\\main\\java\\" +
                    "awesoma\\common\\commands\\" +
                    "command_info.txt";

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static HashSet<Integer> identifyIds(Vector<Movie> collection) {
        HashSet<Integer> uniqueIds = new HashSet<>();
        for (Movie m : collection) {
            uniqueIds.add(m.getId());
        }
        return uniqueIds;
    }

    public static void main(String[] args) {
        Person operator = new Person(
                "John",
                new Date(),
                3F, Color.RED, Country.FRANCE
        );

        Movie m1 = new Movie(
                1, "Rambo", 2,
                3, 4L,
                new Coordinates((double) 1L, 2),
                LocalDateTime.now(),
                MovieGenre.HORROR,
                operator
        );

        Movie m2 = new Movie(
                2, "Mamba", 2,
                1, 3L,
                new Coordinates((double) 1L, 2),
                LocalDateTime.now(),
                MovieGenre.MUSICAL,
                operator
        );

        Movie m3 = new Movie(
                3, "Jango", 2,
                5, 10L,
                new Coordinates((double) 1L, 2),
                LocalDateTime.now(),
                MovieGenre.COMEDY,
                operator
        );

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Vector<Movie> collection = new Vector<>();
        CommandManager commandManager = new CommandManager();
        HashSet<Integer> idList = identifyIds(collection);

        collection.add(m1);
        collection.add(m3);
        collection.add(m2);

        for (Movie m : collection) {
            idList.add(m.getId());
        }

        UniqueIdGenerator idGenerator = new UniqueIdGenerator(idList);

        /* TODO
            save
            execute_script file_name
            add_if_max {element}
         */

        Help help = new Help(HELP_FILE_PATH);
        Info info = new Info(collection, initDate);
        Show show = new Show(collection);
        Exit exit = new Exit();
        Clear clear = new Clear(collection);
        RemoveAt removeAt = new RemoveAt(collection);
        RemoveById removeById = new RemoveById(collection);
        Sort sort = new Sort(collection);
        PrintFieldAscendingTotalBoxOffice printFieldAscendingTotalBoxOffice =
                new PrintFieldAscendingTotalBoxOffice(collection);
        PrintFieldDescendingUsaBoxOffice printFieldDescendingUsaBoxOffice =
                new PrintFieldDescendingUsaBoxOffice(collection);
        PrintFieldDescendingGenre printFieldDescendingGenre =
                new PrintFieldDescendingGenre(collection);
        Add add = new Add(reader, idGenerator, collection);
        UpdateId updateId = new UpdateId(collection, reader);

        Command[] commandsToReg = {
                help,
                info,
                show,
                exit,
                clear,
                removeAt,
                removeById,
                sort,
                printFieldAscendingTotalBoxOffice,
                printFieldDescendingUsaBoxOffice,
                printFieldDescendingGenre,
                add,
                updateId
        };

        commandManager.registerCommands(new ArrayList<>(Arrays.asList(commandsToReg)));

        Console console = new Console(
                commandManager.getRegisteredCommands(),
                commandManager,
                reader,
                collection
        );

        console.interactiveMode();
    }
}
