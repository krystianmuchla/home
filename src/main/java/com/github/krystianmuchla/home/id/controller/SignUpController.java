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

public class SignUpController extends Controller {
    public SignUpController() {
        super("/id/sign_up");
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
                Style.SIGN_UP_FORM
            ),
            Set.of(Script.SIGN_UP_FORM),
            Set.of(Component.LABELED_TEXT_INPUT),
            div(attrs(clazz("background")),
                div(attrs(clazz("modal")),
                    div(attrs(clazz("box sign-up-form")),
                        labeledTextInput("Login", "login", "text"),
                        labeledTextInput("Password", "password", "password"),
                        button(attrs(id("sign-up"), clazz("main-button")),
                            "Sign up"
                        ),
                        a(attrs(href("sign_in")),
                            "Already have an account?"
                        )
                    )
                )
            )
        );
    }
}
