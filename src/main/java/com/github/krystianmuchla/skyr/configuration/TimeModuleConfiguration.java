package com.github.krystianmuchla.skyr.configuration;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Configuration
public class TimeModuleConfiguration {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    @Bean
    public JavaTimeModule timeModule() {
        final var module = new JavaTimeModule();
        module.addSerializer(new StdSerializer<>(Instant.class) {
            @Override
            public void serialize(Instant time, JsonGenerator generator, SerializerProvider provider) throws IOException {
                generator.writeString(formatter.format(time));
            }
        });
        module.addDeserializer(ZonedDateTime.class, new StdDeserializer<>(ZonedDateTime.class) {
            @Override
            public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
                final var time = parser.getValueAsString();
                return ZonedDateTime.parse(time).truncatedTo(ChronoUnit.MILLIS);
            }
        });
        return module;
    }
}
