package com.github.krystianmuchla.home.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.util.Resource;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class FontController extends Controller {
    public static final String PATH = "/font";

    public FontController() {
        super(PATH);
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        try (var inputStream = Resource.inputStream("ui/font/Rubik-Regular.ttf")) {
            ResponseWriter.writeStream(exchange, 200, inputStream, null);
        }
    }
}
