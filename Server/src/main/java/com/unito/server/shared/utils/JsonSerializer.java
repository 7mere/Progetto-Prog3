package com.unito.server.shared.utils;

//import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


 // Classe Utility per JSON serialization and deserialization.
 // Crea una sola istanza del singleton ObjectMapper configurato per il protocollo.
public class JsonSerializer {
    private static final ObjectMapper objectMapper;

    static { // eseguito una sola volta quando la classe viene caricata
        objectMapper = new ObjectMapper();

        // Abilita la formatazzione di JSON più leggibile
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Non lancia errore se nel JSON ci sono campi che non esistono nella classe
        // objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Handle Java 8 date/time types
        // Abilita il supporto per i nuovi tipi di data
        objectMapper.registerModule(new JavaTimeModule());
        // objectMapper.registerModule(new Jdk8Module());
        
        // Scrive date con formato ISO-8601 (più leggibile in JSON)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonSerializer() {
        // Serve a impedire la creazione di oggetti. (istanze di questa classe)
        // Perché la classe è pensata solo con metodi statici.
    }

    /**
     * Serializzare un oggetto in JSON.
     *
     * @param <T> il tipo del parametro
     * @param obj l'oggetto da serializzare
     * @return JSON 
     * @throws RuntimeException se fallisce la serializzazione
     */
    public static <T> String serialize(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializzazione un JSON ad un oggetto.
     *
     * @param json la stringa JSON
     * @param targetClass la classe in cui viene deserializzata
     * @param <T> il tipo del parametro
     * @return l'oggetto deserializzato
     * @throws RuntimeException se fallisce la serializzazione
     */
    public static <T> T deserialize(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Ottiene il ObjectMapper per.
     *
     * @return l'istanza ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
