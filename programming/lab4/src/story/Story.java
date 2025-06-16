package story;

import exceptions.checked.PasswdNotFoundException;
import exceptions.unchecked.UnableToContinueStoryException;

import java.util.Objects;

public class Story {
    public class Valve {
        private int isOpen;

        public void open() {
            isOpen = 1;
        }
    }
    public static class Password {
        public static void check(String path) throws PasswdNotFoundException {
            if (!Objects.equals(path, "docs/passwd")) {
                throw new PasswdNotFoundException("Пароль неверен или не найден");
            }
        }
    }

    public void continueStory(int contFlag, Valve valve) throws UnableToContinueStoryException {
        if (contFlag == 1) {
            valve.open();
        } else {
            throw new UnableToContinueStoryException("История остановлена", story, contFlag);
        }
    }
}
