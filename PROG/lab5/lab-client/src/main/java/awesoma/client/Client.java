package awesoma.client;

import awesoma.common.commands.*;
import awesoma.common.exceptions.EnvVariableNotFoundException;
import awesoma.common.exceptions.ValidationException;
import awesoma.common.models.Movie;
import awesoma.common.util.UniqueIdGenerator;
import awesoma.common.util.Validator;
import awesoma.common.util.json.DumpManager;
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
 *
 * @author awesoma31
 */
public final class Client {
    private static final String ENV = "lab5";
    private static final Date initDate = new Date();

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    /**
     * main method
     */
    public static void main(String[] args) {
        try {
            BufferedReader defReader = new BufferedReader(new InputStreamReader(System.in));
            Validator validator = new Validator();
            DumpManager dumpManager = new DumpManager(System.getenv(ENV), validator);
            Vector<Movie> collection = dumpManager.readCollection();
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
            Add add = new Add(idGenerator, collection);
            UpdateId updateId = new UpdateId(collection, defReader);
            AddIfMax addIfMax = new AddIfMax(idGenerator, collection);
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
            for (Command c : commandsToReg) {
                c.setDefaultReader(defReader);
                c.setReader(defReader);
            }

            Console console = new Console(
                    commandsToReg,
                    defReader
            );
            help.setRegisteredCommands(console.getRegisteredCommands());
            executeScript.setRegisteredCommands(console.getRegisteredCommands());

            console.interactiveMode();

        } catch (JsonSyntaxException e) {
            System.err.println("Exception while trying to validate collection data: " + e.getMessage());
            System.exit(1);
        } catch (DateTimeParseException e) {
            System.err.println("Exception while trying to validate creation time: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (EnvVariableNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ValidationException e) {
            System.err.println(e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("[FAIL]: This command is not recognised");
        }
    }
}
