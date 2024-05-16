package org.awesoma.common;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.awesoma.common.commands.*;

import java.util.HashMap;

/**
 * This class represents common fields required for both sides
 */
public class Environment {
    private static final String DEFAULT_DB_CONFIG_FILE_PATH = "db.cfg";
    private static final HashMap<String, Command> AVAILABLE_COMMANDS = new HashMap<>();
    public static int PORT = 8000;
    public static String HOST = "localhost";
    private static String DB_CONFIG_FILE_PATH = "db.cfg";
    private static String DB_URL;

    // GOVNOCODE
    static {
        registerCommands();
        loadDBURL();
    }

    /**
     * loads DB URL from .env file
     */
    private static void loadDBURL() {
        try {
            Dotenv dotenv = Dotenv.load();
            DB_URL = dotenv.get("DB_URL");
        } catch (DotenvException e) {
            System.err.println("<.env> file not found");
            System.exit(1);
        }
    }

    public static void setPORT(int PORT) {
        Environment.PORT = PORT;
    }

    public static void setHOST(String HOST) {
        Environment.HOST = HOST;
    }

    public static HashMap<String, Command> getAvailableCommands() {
        return AVAILABLE_COMMANDS;
    }

    public static void register(Command command) {
        AVAILABLE_COMMANDS.put(command.getName(), command);
    }

    public static void registerCommands() {
        register(new HelpCommand());
        register(new ShowCommand());
        register(new ExitCommand());
        register(new AddCommand());
        register(new InfoCommand());
        register(new ClearCommand());
        register(new SortCommand());
        register(new PrintFieldAscendingTBOCommand());
        register(new UpdateIdCommand());
        register(new RemoveAtCommand());
        register(new RemoveByIdCommand());
        register(new AddIfMaxCommand());

        register(new LoginCommand());
        register(new RegisterCommand());
    }

    public static String getDbConfigFilePath() {
        return DB_CONFIG_FILE_PATH;
    }

    public static void setDbConfigFilePath(String dbConfigFilePath) {
        DB_CONFIG_FILE_PATH = dbConfigFilePath;
    }

    public static String getDefaultDbConfigFilePath() {
        return DEFAULT_DB_CONFIG_FILE_PATH;
    }

    public static String getDbUrl() {
        return DB_URL;
    }
}
