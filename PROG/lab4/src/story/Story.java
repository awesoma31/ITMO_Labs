package story;

import java.util.Scanner;
import errors.unchecked.UnStartAbleException;

public class Story {
    public void simulate() {
        try {
            Scanner in = new Scanner(System.in);

            System.out.println("Введите START, чтобы начать симуляцию");
            String s = in.nextLine();
            if (!s.equals("START")) {
                throw new UnStartAbleException("Ошибка начала симуляции");
            }
        } catch (UnStartAbleException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }


    }
}
