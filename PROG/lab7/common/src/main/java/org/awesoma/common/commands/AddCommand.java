package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Ask;
import org.awesoma.common.util.Asker;

import java.util.ArrayList;

public class AddCommand extends Command implements Ask {
    public static String NAME = "add";

    public AddCommand() {
        super(AddCommand.NAME, "adds an element to the collection");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME, askMovie(new Asker(reader)));
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        // todo args give
        return visitor.visit(this, request);
    }
}
