package com.goeey.backend.util;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class SerializationUtil {

    private static final Gson gson = new Gson();

    // Serialize to JSON
    public static byte[] serialize(Object obj) {
        return gson.toJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    // Serialize to JSON String
    public static String serializeString(Object obj) {
        return gson.toJson(obj);
    }

    // Deserialize from JSON
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
    }
}