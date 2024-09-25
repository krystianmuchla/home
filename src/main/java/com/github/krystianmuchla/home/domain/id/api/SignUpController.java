package com.github.krystianmuchla.home.domain.id.api;

import com.github.krystianmuchla.home.application.html.Script;
import com.github.krystianmuchla.home.application.html.Style;
import com.github.krystianmuchla.home.application.html.component.Component;
import com.github.krystianmuchla.home.infrastructure.http.Controller;
import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

import static com.github.krystianmuchla.home.application.html.Attribute.*;
import static com.github.krystianmuchla.home.application.html.Html.document;
import static com.github.krystianmuchla.home.application.html.Tag.*;
import static com.github.krystianmuchla.home.application.html.component.LabeledTextInput.labeledTextInput;

public class SignUpController extends Controller {
    public static final String PATH = "/id/sign_up";

    public SignUpController() {
        super(PATH);
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        ResponseWriter.writeHtml(exchange, 200, html());
    }

    private String html() {
        return document(
            Set.of(
                Style.BACKGROUND,
                Style.MAIN_BUTTON,
                Style.MODAL,
                Style.ON_TOP,
                Style.SIGN_UP_FORM
            ),
            Set.of(Script.SIGN_UP_FORM),
            Set.of(Component.LABELED_TEXT_INPUT, Component.TOAST),
            div(attrs(clazz("background")),
                div(attrs(clazz("modal")),
                    div(attrs(clazz("on-top sign-up-form")),
                        labeledTextInput("Name", "name", "text"),
                        labeledTextInput("Login", "login", "text"),
                        labeledTextInput("Password", "password", "password"),
                        button(attrs(id("sign-up"), clazz("main-button")),
                            "Sign up"
                        ),
                        a(attrs(href(SignInController.PATH)),
                            "Already have an account?"
                        )
                    )
                )
            )
        );
    }
}
