package org.awesoma.common.commands;

import org.awesoma.common.Request;
import org.awesoma.common.Response;

import java.util.ArrayList;

public interface Command {
    public Request build();
    public Response execute(ArrayList<String> args);
}
