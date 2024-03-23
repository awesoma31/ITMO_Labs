package org.awesoma.common.commands;

/**
 * this command closes the program without saving the collection to file, same as Exit command just by 'q'
 */
public class Quit extends Exit {
    public Quit() {
        super("q", "This commands terminates proccess");
    }
}
