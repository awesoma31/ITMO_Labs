package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class PrintFieldAscendingTBOCommand extends Command {
    public static String name = "print_field_ascending_total_box_office";

    public PrintFieldAscendingTBOCommand() {
        super(PrintFieldAscendingTBOCommand.name, "prints fields ascending TBO");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(name);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this);
    }
}
