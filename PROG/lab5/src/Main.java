import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Вы не ввели имя файла с данными как аргумент командной строки");
            System.exit(1);
        }

        String modelDataPath = args[0];


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
