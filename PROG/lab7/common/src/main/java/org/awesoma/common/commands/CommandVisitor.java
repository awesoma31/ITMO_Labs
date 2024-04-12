package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

public interface CommandVisitor {
    //    interface CommandVisitor {
    Response visit(Help help);

    Response visit(Info info);

    Response visit(Show show);

    Response visit(Add add, Request request);

    Response visit(Clear clear);

    Response visit(Sort sort);

    Response visit(PrintFieldAscendingTBO printFieldAscendingTBO);

    Response visit(UpdateId updateId, Request request);

    Response visit(RemoveById removeById, Request request);

    Response visit(RemoveAt removeAt, Request request);

    Response visit(AddIfMax addIfMax, Request request);

    Response visit(Save save);

    Response visit(Exit exit);
//    }
}
