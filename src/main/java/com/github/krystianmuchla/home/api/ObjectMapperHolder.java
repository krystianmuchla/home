package com.github.krystianmuchla.home.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ObjectMapperHolder {
    public static final ObjectMapper INSTANCE;

    static {
        INSTANCE = new ObjectMapper();
        INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        INSTANCE.registerModule(createTimeModule());
    }

    private static JavaTimeModule createTimeModule() {
        final var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
        final var module = new JavaTimeModule();
        module.addSerializer(new StdSerializer<>(Instant.class) {
            @Override
            public void serialize(
                final Instant time,
                final JsonGenerator generator,
                final SerializerProvider provider
            ) throws IOException {
                generator.writeString(formatter.format(time));
            }
        });
        module.addSerializer(new StdSerializer<>(ZonedDateTime.class) {
            @Override
            public void serialize(
                final ZonedDateTime time,
                final JsonGenerator generator,
                final SerializerProvider provider
            ) throws IOException {
                generator.writeString(formatter.format(time.toInstant()));
            }
        });
        module.addDeserializer(ZonedDateTime.class, new StdDeserializer<>(ZonedDateTime.class) {
            @Override
            public ZonedDateTime deserialize(
                final JsonParser parser,
                final DeserializationContext context
            ) throws IOException {
                final var time = parser.getValueAsString();
                return ZonedDateTime.parse(time).truncatedTo(ChronoUnit.MILLIS);
            }
        });
        return module;
    }
}
