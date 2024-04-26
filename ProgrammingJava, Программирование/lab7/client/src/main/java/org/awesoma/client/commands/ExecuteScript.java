package org.awesoma.client.commands;

import org.awesoma.common.commands.Command;
import org.awesoma.common.commands.CommandVisitor;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

// ALERT!!! GOVNOCODE
// его еще в хелпе не видно
public class ExecuteScript extends Command {
    public static final String NAME = "execute_script";

    public ExecuteScript() {
        super(NAME, "executes script from file");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        throw new RuntimeException("Execute script can't build request");
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        throw new RuntimeException("Execute script can't accept visitor");
    }
}
