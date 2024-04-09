package org.awesoma;

import org.awesoma.common.commands.AbstractCommand;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class ExecuteScript extends AbstractCommand {
    public static final String NAME = "execute_script";

    public ExecuteScript() {
        super(NAME, "executes script from file");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        // todo
        return new Request(NAME);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return null;
    }
}
