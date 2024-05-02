package org.awesoma.common.util;

import java.io.*;

/**
 * Class responsible for data serialization\deserialization by using java ObjectsInput\OutputStream
 */
public class DataSerializer {
    /**
     * deserialize byte data to class
     * @param byteData to deserialize
     * @param clazz transform to
     * @return deserialized class
     */
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

    /**
     * serialize class to byte array
     * @param obj to serialize
     * @return byte array
     */
    public static <T> byte[] serialize(T obj) throws IOException {
        var byteOut = new ByteArrayOutputStream();
        var objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        objOut.flush();
        return byteOut.toByteArray();
    }
}
