package org.awesoma.server.util;

import org.awesoma.common.Response;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ResponseSender {
    private ObjectOutputStream objOut;

    public ResponseSender(ObjectOutputStream objOut) {
        this.objOut = objOut;
    }

    public void sendResponse(Response response) throws IOException {
        objOut.writeObject(response);
    }

    public void setObjOut(ObjectOutputStream objOut) {
        this.objOut = objOut;
    }
}
