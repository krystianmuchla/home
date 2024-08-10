package com.github.krystianmuchla.home.api;

import com.github.krystianmuchla.home.exception.http.BadRequestException;
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
            var string = element.getAsJsonPrimitive().getAsString();
            try {
                return Instant.parse(string);
            } catch (DateTimeParseException exception) {
                throw new BadRequestException(exception);
            }
        };
    }
}
