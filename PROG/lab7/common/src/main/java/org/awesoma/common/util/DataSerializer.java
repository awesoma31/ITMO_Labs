package org.awesoma.common.util;

import java.io.*;

public class DataSerializer {
    public static <T> T deserialize(byte[] byteData, Class<T> clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteData);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return clazz.cast(obj);
    }

    public static <T> byte[] serialize(T obj) throws IOException {
        var byteOut = new ByteArrayOutputStream();
        var objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        objOut.flush();
        return byteOut.toByteArray();
    }
}
