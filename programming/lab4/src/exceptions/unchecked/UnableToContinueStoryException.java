package exceptions.unchecked;

import story.Story;

public class UnableToContinueStoryException extends RuntimeException{
    public String message;
    public Story story;
    public int flag;


    public UnableToContinueStoryException(String message, Story story, int flag) {
        this.flag = flag;
        this.story = story;
        this.message = message;
    }


    @Override
    public String getMessage() {
        return "Unexpected flag: " + flag + " for Story: " + story.toString() + ". " + message;
    }
}
