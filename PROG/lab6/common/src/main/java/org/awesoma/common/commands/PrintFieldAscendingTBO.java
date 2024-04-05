package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.util.List;

public class PrintFieldAscendingTBO extends AbstractCommand{
    public static String name = "print_field_ascending_total_box_office";
    public PrintFieldAscendingTBO() {
        super(PrintFieldAscendingTBO.name, "prints fields ascending TBO");
    }

    @Override
    public Request buildRequest(List<String> args) {
        return new Request(name);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }

    @Override
    public void handleResponse(Response response) {
        System.out.println(response.getMessage());
    }
}
