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

import static com.github.krystianmuchla.home.html.Attribute.attrs;
import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.*;

public class SignInController extends Controller {
    public SignInController() {
        super("/id/sign_in");
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
                Style.SIGN_IN_FORM
            ),
            List.of(Script.SIGN_IN_FORM),
            List.of(login, password),
            div(attrs("class", "background"),
                div(attrs("class", "modal"),
                    div(attrs("class", "box sign-in-form"),
                        login.tag(),
                        password.tag(),
                        button(attrs("id", "sign-in", "class", "main-button"),
                            "Sign in"
                        ),
                        a(attrs("href", "sign_up"),
                            "No account?"
                        )
                    )
                )
            )
        );
    }
}
