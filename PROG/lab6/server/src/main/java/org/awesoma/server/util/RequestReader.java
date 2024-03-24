package org.awesoma.server.util;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.models.Movie;
import org.awesoma.server.commands.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class RequestReader {
    private ObjectInputStream objIn;

    public RequestReader(ObjectInputStream objIn) {
        this.objIn = objIn;
    }


    public Request readRequest() throws IOException, ClassNotFoundException {
        return (Request) objIn.readObject();
    }

    public void setObjIn(ObjectInputStream objIn) {
        this.objIn = objIn;
    }
}
