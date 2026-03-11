package com.unito.client.shared.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * Utility class for JSON serialization and deserialization.
 * Provides singleton ObjectMapper instance configured for the protocol.
 */
public class JsonSerializer {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // Enable pretty printing for debugging
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        // Write dates as ISO-8601 strings
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonSerializer() {
        // Prevent instantiation
    }

    /**
     * Serialize an object to JSON string.
     *
     * @param obj the object to serialize
     * @return JSON string representation
     * @throws RuntimeException if serialization fails
     */
    public static String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object: " + e.getMessage(), e);
        }
    }

    /**
     * Deserialize a JSON string to an object.
     *
     * @param json the JSON string
     * @param targetClass the class to deserialize to
     * @param <T> the type parameter
     * @return deserialized object
     * @throws RuntimeException if deserialization fails
     */
    public static <T> T deserialize(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Get the underlying ObjectMapper for advanced usage.
     *
     * @return the ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
