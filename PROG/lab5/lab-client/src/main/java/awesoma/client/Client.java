package awesoma.client;

import awesoma.common.commands.*;
import awesoma.common.exceptions.ValidationException;
import awesoma.common.models.Movie;
import awesoma.common.util.UniqueIdGenerator;
import awesoma.common.util.json.DumpManager;
import awesoma.common.util.json.Validator;
import awesoma.managers.Console;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Vector;

/**
 * main class that represents user
 * @author awesoma31
 */
public final class Client {
    /*
    represents environment name where to find nodel description
     */
    private static final String ENV = "lab5";
    /*
    initialization date
     */
    public static Date initDate = new Date();
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    /**
     *
     * @return String path from env name
     */
    private static String getPathFromEnv() {
        return System.getenv(ENV);
    }

    /**
     * main method
     */
    public static void main(String[] args)  {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Validator validator = new Validator();
        DumpManager dumpManager = new DumpManager(System.getenv(ENV), validator);

        Vector<Movie> collection = null;
        try {
            collection = dumpManager.readCollection();
        } catch (JsonSyntaxException | ValidationException e) {
            System.out.println("Exception while trying to validate collection data: " + e.getMessage());
            System.exit(1);
        } catch (DateTimeParseException e) {
            System.out.println("Exception while trying to validate creation time: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UniqueIdGenerator idGenerator = new UniqueIdGenerator(UniqueIdGenerator.identifyIds(collection));

        Help help = new Help();
        Info info = new Info(collection, initDate);
        Show show = new Show(collection);
        Exit exit = new Exit();
        Quit quit = new Quit();
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
        AddIfMax addIfMax = new AddIfMax(reader, idGenerator, collection);
        Save save = new Save(collection, dumpManager);
        ExecuteScript executeScript = new ExecuteScript();


        Command[] commandsToReg = {
                help,
                info,
                show,
                exit,
                quit,
                clear,
                removeAt,
                removeById,
                sort,
                printFieldAscendingTotalBoxOffice,
                printFieldDescendingUsaBoxOffice,
                printFieldDescendingGenre,
                add,
                updateId,
                addIfMax,
                save,
                executeScript
        };

        Console console = new Console(
                commandsToReg,
                reader,
                collection
        );
        help.setRegisteredCommands(console.getRegisteredCommands());
        executeScript.setRegisteredCommands(console.getRegisteredCommands());

        console.interactiveMode();
    }
}
