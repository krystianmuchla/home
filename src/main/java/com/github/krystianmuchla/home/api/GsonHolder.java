package com.github.krystianmuchla.home.api;

import com.google.gson.*;

import java.time.Instant;

public class GsonHolder {
    public static final Gson INSTANCE = new GsonBuilder()
        .registerTypeAdapter(Instant.class, instantSerializer())
        .registerTypeAdapter(Instant.class, instantDeserializer())
        .create();

    private static JsonSerializer<Instant> instantSerializer() {
        return (instant, type, context) -> new JsonPrimitive(instant.toString());
    }

    private static JsonDeserializer<Instant> instantDeserializer() {
        return (element, type, context) -> {
            final var string = element.getAsJsonPrimitive().getAsString();
            return Instant.parse(string);
        };
    }
}
