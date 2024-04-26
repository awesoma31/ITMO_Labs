package org.awesoma.common.network;

import org.awesoma.common.UserCredentials;
import org.awesoma.common.models.Movie;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {
    private final String commandName;
    private final Movie movie;
    private final ArrayList<String> args;
    private UserCredentials userCredentials;


    public Request(String commandName, Movie movie) {
        this.commandName = commandName;
        this.movie = movie;
        args = null;
    }

    public Request(String commandName, ArrayList<String> args) {
        this.commandName = commandName;
        this.args = args;
        this.movie = null;
    }

    public Request(String commandName) {
        this.commandName = commandName;
        args = null;
        movie = null;
    }

    public Request(String commandName, Movie movie, ArrayList<String> args) {
        this.commandName = commandName;
        this.movie = movie;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public Movie getMovie() {
        return movie;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }
}
