package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

public interface CommandVisitor {
    //    interface CommandVisitor {
    Response visit(HelpCommand help);

    Response visit(InfoCommand info);

    Response visit(ShowCommand show);

    Response visit(AddCommand add, Request request);

    Response visit(ClearCommand clear);

    Response visit(SortCommand sort);

    Response visit(PrintFieldAscendingTBOCommand printFieldAscendingTBO);

    Response visit(UpdateIdCommand updateId, Request request);

    Response visit(RemoveByIdCommand removeById, Request request);

    Response visit(RemoveAtCommand removeAt, Request request);

    Response visit(AddIfMaxCommand addIfMax, Request request);

    Response visit(SaveCommand save);

    Response visit(ExitCommand exit);
//    }
}
