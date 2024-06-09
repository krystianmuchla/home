package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.exception.http.UnauthorizedException;
import com.github.krystianmuchla.home.html.Script;
import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.element.LabeledTextInput;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

import static com.github.krystianmuchla.home.html.Attribute.attributes;
import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.*;

public class SignUpController extends Controller {
    public SignUpController() {
        super("/id/sign_up");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        try {
            session(exchange);
            ResponseWriter.writeLocation(exchange, "/drive");
            ResponseWriter.write(exchange, 302);
        } catch (final UnauthorizedException exception) {
            ResponseWriter.writeHtml(exchange, 200, html());
        }
    }

    private String html() {
        final var login = new LabeledTextInput("Login", "login", "text");
        final var password = new LabeledTextInput("Password", "password", "password");
        return document(
            List.of(
                Style.BACKGROUND,
                Style.BOX,
                Style.MAIN_BUTTON,
                Style.MODAL,
                Style.SIGN_UP_FORM
            ),
            List.of(Script.SIGN_UP_FORM),
            List.of(login, password),
            div(attributes("class", "background"),
                div(attributes("class", "modal"),
                    div(attributes("class", "box sign-up-form"),
                        login.tag(),
                        password.tag(),
                        button(attributes("id", "sign-up", "class", "main-button"),
                            "Sign up"
                        ),
                        a(attributes("href", "sign_in"),
                            "Already have an account?"
                        )
                    )
                )
            )
        );
    }
}
