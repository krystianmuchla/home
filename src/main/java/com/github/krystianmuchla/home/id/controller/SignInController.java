package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.exception.http.UnauthorizedException;
import com.github.krystianmuchla.home.html.Script;
import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.component.Component;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Attribute.*;
import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.*;
import static com.github.krystianmuchla.home.html.component.LabeledTextInput.labeledTextInput;

public class SignInController extends Controller {
    public SignInController() {
        super("/id/sign_in");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        try {
            session(exchange);
            ResponseWriter.writeLocation(exchange, 302, "/drive");
        } catch (final UnauthorizedException exception) {
            ResponseWriter.writeHtml(exchange, 200, html());
        }
    }

    private String html() {
        return document(
            Set.of(
                Style.BACKGROUND,
                Style.BOX,
                Style.MAIN_BUTTON,
                Style.MODAL,
                Style.SIGN_IN_FORM
            ),
            Set.of(Script.SIGN_IN_FORM),
            Set.of(
                Component.HTTP,
                Component.LABELED_TEXT_INPUT,
                Component.TOAST
            ),
            div(attrs(clazz("background")),
                div(attrs(clazz("modal")),
                    div(attrs(clazz("box sign-in-form")),
                        labeledTextInput("Login", "login", "text"),
                        labeledTextInput("Password", "password", "password"),
                        button(attrs(id("sign-in"), clazz("main-button")),
                            "Sign in"
                        ),
                        a(attrs(href("sign_up")),
                            "No account?"
                        )
                    )
                )
            )
        );
    }
}
