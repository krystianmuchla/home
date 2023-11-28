package com.example.skyr.configuration

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Configuration
class TimeModuleConfiguration {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)

    @Bean
    fun timeModule(): JavaTimeModule {
        val module = JavaTimeModule()
        module.addSerializer(object : StdSerializer<Instant>(Instant::class.java) {
            override fun serialize(time: Instant, generator: JsonGenerator, provider: SerializerProvider) {
                generator.writeString(formatter.format(time))
            }
        })
        module.addDeserializer(
            ZonedDateTime::class.java,
            object : StdDeserializer<ZonedDateTime>(ZonedDateTime::class.java) {
                override fun deserialize(parser: JsonParser, context: DeserializationContext): ZonedDateTime? {
                    val time = parser.readValueAs(String::class.java)
                    return ZonedDateTime.parse(time).truncatedTo(ChronoUnit.MILLIS)
                }
            })
        return module
    }
}
