package com.goeey.backend.util;

import java.io.*;

public class SerializationUtil {
    // Serialize to byte array
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
        return bos.toByteArray();
    }

    // Deserialize from byte array
    // Generic method for deserialization
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();

        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        } else {
            throw new ClassNotFoundException("Failed to deserialize object as instance of " + clazz.getName());
        }
    }
}