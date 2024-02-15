package awesoma.client;

import awesoma.common.commands.*;
import awesoma.common.managers.CommandManager;
import awesoma.common.models.*;

import java.time.LocalDateTime;
import java.util.*;


public final class Client {

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }


    public static void main(String[] args) {
        Person operator = new Person(
                "John",
                LocalDateTime.now(),
                3.0, Color.GREEN, Country.USA
        );

        Movie m1 = new Movie(
                1, "Rambo", 2,
                3L, 4.0F,
                new Coordinates(1L, 2F),
                new Date(),
                MovieGenre.COMEDY,
                operator
        );

        Movie m2 = new Movie(
                2, "Mamba", 2,
                3L, 4.0F,
                new Coordinates(1L, 2F),
                new Date(),
                MovieGenre.COMEDY,
                operator
        );

        Movie m3 = new Movie(
                3, "Mamba", 2,
                3L, 4.0F,
                new Coordinates(1L, 2F),
                new Date(),
                MovieGenre.COMEDY,
                operator
        );

        TreeSet<Movie> collection = new TreeSet<>();

        CommandManager commandManager = new CommandManager();

        collection.add(m1);
        collection.add(m2);
        collection.add(m3);

        // TODO commands:
        //  save,
        //  executeScript,
        //  countByOperator,
        //  filter_starts_with_name

        Help helpCommand = new Help();
        Info infoCommand = new Info();
        Show showCommand = new Show();
        RemoveById removeByIdCommand = new RemoveById();
        Clear clearCommand = new Clear();
        Exit exitCommand = new Exit();
        AddIfMax addIfMaxCommand = new AddIfMax();
        Add addCommand = new Add();
        Update updateCommand = new Update();
        RemoveGreater removeGreaterCommand = new RemoveGreater();
        CountByOperator countByOperatorCommand = new CountByOperator();
        CountLessThanOscarsCount countLessThanOscarsCountCommand = new CountLessThanOscarsCount();
        FilterStartsWithName filterStartsWithNameCommand = new FilterStartsWithName();
        History historyCommand = new History();

        Command[] commandsToReg = {
                helpCommand,
                infoCommand,
                showCommand,
                removeByIdCommand,
                clearCommand,
                exitCommand,
                addIfMaxCommand,
                addCommand,
                updateCommand,
                removeGreaterCommand,
                countLessThanOscarsCountCommand,
                countByOperatorCommand,
                filterStartsWithNameCommand,
                historyCommand
        };
        commandManager.registerCommands(new ArrayList<>(Arrays.asList(commandsToReg)));

    }
}
