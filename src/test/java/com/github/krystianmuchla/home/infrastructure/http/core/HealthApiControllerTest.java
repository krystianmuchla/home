package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.AppContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HealthApiControllerTest {

    @BeforeAll
    static void beforeAllTests() {
        AppContext.init();
    }

    @Test
    void shouldGetHealth() throws URISyntaxException, IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(new URI(AppContext.HOST + "/api/health"))
            .GET()
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).isEqualTo(Optional.of("application/json"));
        assertThat(response.body()).isEqualTo("{}");
    }
}
