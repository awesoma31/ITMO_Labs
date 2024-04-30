package org.awesoma.common.util;

import java.io.*;

public class DataSerializer {
    public static <T> T deserialize(byte[] byteData, Class<T> clazz) throws IOException {
        try {
            var bis = new ByteArrayInputStream(byteData);
            var ois = new ObjectInputStream(bis);
            var obj = ois.readObject();
            ois.close();
            return clazz.cast(obj);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> byte[] serialize(T obj) throws IOException {
        var byteOut = new ByteArrayOutputStream();
        var objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        objOut.flush();
        return byteOut.toByteArray();
    }
}
