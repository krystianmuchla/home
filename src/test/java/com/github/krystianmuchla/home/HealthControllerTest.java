package com.github.krystianmuchla.home;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @BeforeAll
    static void beforeAllTests() throws Exception {
        AppContext.init();
    }

    @Test
    void shouldGetHealth() throws URISyntaxException, IOException, InterruptedException {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/health"))
            .GET()
            .build();

        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        assertThat(response.body()).isEqualTo("{}");
    }
}
