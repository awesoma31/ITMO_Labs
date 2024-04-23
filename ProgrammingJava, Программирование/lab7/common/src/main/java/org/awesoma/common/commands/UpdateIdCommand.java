package org.awesoma.common.commands;

import org.awesoma.common.models.Movie;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Ask;
import org.awesoma.common.util.Asker;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class UpdateIdCommand extends Command implements Ask {
    public static final String NAME = "update_id";

    public UpdateIdCommand() {
        super(NAME, "updates element with given id");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        Movie movie = askMovie(new Asker(reader));
        movie.setCreationDate(LocalDateTime.now());
        return new Request(NAME, movie, args);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        this.userCredentials = request.getUserCredentials();
        return visitor.visit(this, request);
    }
}
