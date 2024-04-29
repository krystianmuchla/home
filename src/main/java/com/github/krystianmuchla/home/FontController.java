package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.util.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FontController extends Controller {
    public static String PATH = "/font";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        try (final var inputStream = Resource.inputStream("ui/font/Rubik-Regular.ttf")) {
            ResponseWriter.writeStream(response, inputStream);
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }
}
