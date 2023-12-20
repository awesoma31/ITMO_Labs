package story;

import exceptions.unchecked.UnableToContinueStoryException;

import java.util.Objects;

public class Story {
    public class Valve {
        private boolean isOpen;

        public void open() {
            isOpen = true;
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
        if (s.equals("да")) {
            valve.open();
        } else {
            throw new UnableToContinueStoryException("История остановлена");
        }
    }
}
