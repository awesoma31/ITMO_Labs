package story;

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
        public static void check(String path){
            if (!Objects.equals(path, "docs/passwd")) {
                throw new UnableToContinueStoryException("Пароль неверен или не найден");
            }
        }
    }

    public void continueStory(int s, Story.Valve valve) throws UnableToContinueStoryException {
        if (s == 1) {
            valve.open();
        } else {
            throw new UnableToContinueStoryException("История остановлена");
        }
    }
}
