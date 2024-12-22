package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;
import com.google.gson.*;

public class GsonHolder {
    public static final Gson INSTANCE = new GsonBuilder()
        .registerTypeAdapter(Time.class, timeSerializer())
        .registerTypeAdapter(Time.class, timeDeserializer())
        .create();

    private static JsonSerializer<Time> timeSerializer() {
        return (time, type, context) -> new JsonPrimitive(time.value().toString());
    }

    private static JsonDeserializer<Time> timeDeserializer() {
        return (element, type, context) -> {
            JsonPrimitive primitive;
            try {
                primitive = element.getAsJsonPrimitive();
            } catch (IllegalStateException exception) {
                throw new BadRequestException(exception);
            }
            try {
                return TimeFactory.create(primitive.getAsString());
            } catch (Exception exception) {
                throw new BadRequestException(exception);
            }
        };
    }
}
