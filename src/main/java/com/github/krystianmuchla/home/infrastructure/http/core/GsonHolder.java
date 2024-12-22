package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;
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
            JsonPrimitive primitive;
            try {
                primitive = element.getAsJsonPrimitive();
            } catch (IllegalStateException exception) {
                throw new BadRequestException(exception);
            }
            try {
                return Instant.parse(primitive.getAsString());
            } catch (DateTimeParseException exception) {
                throw new BadRequestException(exception);
            }
        };
    }
}
