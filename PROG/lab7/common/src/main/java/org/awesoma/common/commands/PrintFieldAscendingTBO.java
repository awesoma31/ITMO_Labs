package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class PrintFieldAscendingTBO extends AbstractCommand {
    public static String name = "print_field_ascending_total_box_office";

    public PrintFieldAscendingTBO() {
        super(PrintFieldAscendingTBO.name, "prints fields ascending TBO");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(name);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
