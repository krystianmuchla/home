package com.github.krystianmuchla.home.api;

import com.github.krystianmuchla.home.exception.RequestException;
import com.google.gson.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;

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
            try {
                return Instant.parse(string);
            } catch (final DateTimeParseException exception) {
                throw new RequestException(exception);
            }
        };
    }
}
