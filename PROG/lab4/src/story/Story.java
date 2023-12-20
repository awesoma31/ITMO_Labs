package story;

import exceptions.unchecked.UnableToContinueStoryException;

import java.util.Objects;

public class Story {
    public class Valve {
        private boolean isOpen;

        public void open() {
            isOpen = true;
        }

        public void close() {
            isOpen = false;
        }

        public void changeStatus() {
            isOpen = !isOpen;
        }

        public boolean isOpen() {
            return isOpen;
        }
    }
    public static class Password {
        protected final static String passwd = "zxc";
        public static void check(String path){
            if (!Objects.equals(path, "docs/passwd")) {
                throw new UnableToContinueStoryException("Пароль неверен или не найден");
            }
        }
    }

    public void continueStory(String s, Story.Valve valve) throws UnableToContinueStoryException {
        if (!s.equals("да")) {
            throw new UnableToContinueStoryException("История остановлена");
        } else {
            valve.open();
        }
    }
}
