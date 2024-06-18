package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.util.Resource;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class FontController extends Controller {
    public FontController() {
        super("/font");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        try (final var inputStream = Resource.inputStream("ui/font/Rubik-Regular.ttf")) {
            ResponseWriter.writeStream(exchange, 200, inputStream, null);
        }
    }
}
