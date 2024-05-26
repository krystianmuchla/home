package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import com.github.krystianmuchla.home.html.Script;
import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.element.LabeledTextInput;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.github.krystianmuchla.home.html.Attribute.attributes;
import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.*;

public class SignUpController extends Controller {
    public static final String PATH = "/id/sign_up";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            session(request);
            response.sendRedirect("/drive");
        } catch (final AuthenticationException exception) {
            ResponseWriter.writeHtml(response, html());
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
