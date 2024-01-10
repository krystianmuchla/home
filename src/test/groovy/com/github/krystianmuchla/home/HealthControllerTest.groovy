package com.github.krystianmuchla.home

import org.junit.jupiter.api.Test

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

import static org.assertj.core.api.Assertions.assertThat

class HealthControllerTest extends AppTest {

    @Test
    void shouldGetHealth() {
        final client = HttpClient.newHttpClient()
        final request = HttpRequest.newBuilder()
            .uri(new URI("$APP_HOST/api/health"))
            .GET()
            .build()

        final response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.headers().firstValue('Content-Type')).isEqualTo(Optional.of('application/json'))
        assertThat(response.body()).isEqualTo('{}')
    }
}
