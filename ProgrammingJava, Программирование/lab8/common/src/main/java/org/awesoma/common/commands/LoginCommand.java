package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.network.Status;

import java.util.ArrayList;

public class LoginCommand extends Command {
    public static final String NAME = "login";

    public LoginCommand() {
        super(NAME, "");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this, request);
    }

    @Override
    public void handleResponse(Response response) {
        if (response.getStatusCode() == Status.ERROR) {
            throw new RuntimeException("Login failed: " + response.getMessage());
        }
    }

    @Override
    public boolean isShowInHelp() {
        return false;
    }
}
