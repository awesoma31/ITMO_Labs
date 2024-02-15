package awesoma.common.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Help extends Command {

    public static void execute() {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\gwert\\Documents\\ITMO_Labs\\PROG\\lab5_archetype\\lab\\lab-common\\src\\main\\java\\awesoma\\common\\commands\\command_info.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Info about commands not found");
            System.exit(1);
        }

    }
}
